package dev.convertly.worker.converter;

import static java.util.stream.Collectors.toMap;

import dev.convertly.common.Format;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ConverterRegistry {

  private record ConversionKey(Format source, Format target) {}

  private final Map<ConversionKey, Converter> converters;

  public ConverterRegistry(List<Converter> allConverters) {
    this.converters =
        Map.copyOf(
            allConverters.stream()
                .flatMap(
                    c ->
                        c.sources().stream()
                            .map(s -> Map.entry(new ConversionKey(s, c.target()), c)))
                .collect(
                    toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                          throw new IllegalStateException(
                              "Two converters for the same pair: "
                                  + a.getClass().getSimpleName()
                                  + " and "
                                  + b.getClass().getSimpleName());
                        })));
  }

  public Converter get(Format from, Format to) {
    Converter converter = converters.get(new ConversionKey(from, to));

    if (converter == null) {
      throw new UnsupportedConversionException(from, to);
    }

    return converter;
  }
}
