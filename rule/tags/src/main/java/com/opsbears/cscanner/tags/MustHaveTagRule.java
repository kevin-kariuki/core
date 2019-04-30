package com.opsbears.cscanner.tags;

import com.opsbears.cscanner.core.RuleResult;
import com.opsbears.webcomponents.immutable.ImmutableArrayList;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class MustHaveTagRule implements TaggedResourceRule {
    public final static String MUST_HAVE_TAG = "MUST_HAVE_TAG";

    private final String tagName;
    private final Set<TaggedResourceType> onlyTypes;
    private final Set<Pattern> include;
    private final Set<Pattern> exclude;

    public MustHaveTagRule(
        Options options
    ) {
        this.tagName = options.tagName;
        this.onlyTypes = options.onlyTypes;
        this.include = options.include;
        this.exclude = options.exclude;
    }

    @Override
    public List<RuleResult> evaluate(TaggedResourceConnection connection) {
        Stream<TaggedResource> stream = connection
            .getTaggedResourceClient()
            .getTaggedResources();
        if (!onlyTypes.isEmpty()) {
            stream = stream.filter(
                item -> onlyTypes.contains(item.resourceType)
            );
        }

        if (!include.isEmpty()) {
            stream = stream.filter(
                item -> include.stream().anyMatch(f -> f.matcher(item.resourceName).matches())
            );
        }

        if (!exclude.isEmpty()) {
            stream = stream.filter(
                item -> exclude.stream().noneMatch(f -> f.matcher(item.resourceName).matches())
            );
        }

        Stream<RuleResult> newStream = stream.map(
            item -> new RuleResult(
                connection.getConnectionName(),
                MUST_HAVE_TAG,
                item.resourceType.toString(),
                item.resourceRegion,
                item.resourceName,
                item.tags.containsKey(tagName) ? RuleResult.Compliancy.COMPLIANT : RuleResult.Compliancy.NONCOMPLIANT,
                item.tags.containsKey(tagName)?new ImmutableArrayList<>():
                    new ImmutableArrayList<RuleResult.Violation>()
                        .withAdd(new RuleResult.Violation(null, "Missing tag " + tagName))
            )
        );

        return newStream.collect(Collectors.toList());
    }

    public class Options {
        final String tagName;
        final Set<TaggedResourceType> onlyTypes;
        final Set<Pattern> include;
        final Set<Pattern> exclude;

        public Options(
            String tagName,
            Set<TaggedResourceType> onlyTypes,
            Set<Pattern> include,
            Set<Pattern> exclude
        ) {
            this.tagName = tagName;
            this.onlyTypes = onlyTypes;
            this.include = include;
            this.exclude = exclude;
        }
    }
}
