package com.btk5h.skriptmirror.skript.custom.expression;

import com.btk5h.skriptmirror.skript.custom.CustomSyntaxSection;
import com.btk5h.skriptmirror.util.SkriptMirrorUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExpressionSyntaxInfo extends CustomSyntaxSection.SyntaxData {
  private final int[] inheritedSingles;
  private final boolean alwaysPlural;
  private final boolean adaptArgument;
  private final boolean property;


  private ExpressionSyntaxInfo(File script, String pattern, int matchedPattern, int[] inheritedSingles,
                               boolean alwaysPlural, boolean adaptArgument, boolean property) {
    super(script, pattern, matchedPattern);
    this.inheritedSingles = inheritedSingles;
    this.alwaysPlural = alwaysPlural;
    this.adaptArgument = adaptArgument;
    this.property = property;
  }

  public static ExpressionSyntaxInfo create(File script, String pattern, int matchedPattern, boolean alwaysPlural,
                                            boolean adaptArgument, boolean property) {
    StringBuilder newPattern = new StringBuilder(pattern.length());
    List<Integer> inheritedSingles = new ArrayList<>();
    String[] parts = pattern.split("%");

    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];
      if (i % 2 == 0) {
        newPattern.append(part);
      } else {
        if (part.startsWith("$")) {
          part = part.substring(1);
          inheritedSingles.add(i / 2);
        }

        if (part.startsWith("_")) {
          part = part.endsWith("s") ? "javaobject" : "javaobjects";
        } else {
          part = SkriptMirrorUtil.replaceUserInputPatterns(part);
        }

        newPattern.append('%');
        newPattern.append(part);
        newPattern.append('%');
      }
    }

    return new ExpressionSyntaxInfo(
        script,
        newPattern.toString(),
        matchedPattern,
        inheritedSingles.stream()
            .mapToInt(i -> i)
            .toArray(),
        alwaysPlural,
        adaptArgument,
        property);
  }

  public int[] getInheritedSingles() {
    return inheritedSingles;
  }

  public boolean isAlwaysPlural() {
    return alwaysPlural;
  }

  public boolean shouldAdaptArgument() {
    return adaptArgument;
  }

  public boolean isProperty() {
    return property;
  }

  @Override
  public String toString() {
    return String.format("%s (singles: %s, plural: %s, adapt: %s, property: %s)",
        getPattern(), Arrays.toString(inheritedSingles), alwaysPlural, adaptArgument, property);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExpressionSyntaxInfo that = (ExpressionSyntaxInfo) o;
    return alwaysPlural == that.alwaysPlural &&
        adaptArgument == that.adaptArgument &&
        property == that.property &&
        Arrays.equals(inheritedSingles, that.inheritedSingles) &&
        Objects.equals(getScript(), that.getScript()) &&
        Objects.equals(getPattern(), that.getPattern());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(alwaysPlural, adaptArgument, property, getScript(), getPattern());
    result = 31 * result + Arrays.hashCode(inheritedSingles);
    return result;
  }
}
