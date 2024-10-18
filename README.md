# About

Example of a [Micronaut](https://micronaut.io/) Java lambda which uses
[Drools](https://www.drools.org/). It dynamically loads .drl using a
rule unit [the modern way of writing Drools
rules](https://docs.drools.org/8.39.0.Final/drools-docs/docs-website/drools/migration-guide/index.html#_rule_unit),
and executes it. Includes caching this to avoid recompilation to
improve speed.

The way this works:

1. Create a rule unit.
2. Load any data.
3. Load the .drl.
4. Execute it.

```java
MeasurementUnit ruleUnitData = new MeasurementUnit();
ruleUnitData.getMeasurements().add(input);

RuleUnitInstance<MeasurementUnit> instance =
  (RuleUnitInstance<MeasurementUnit>) newInstance(
    "src/main/resources/example/micronaut/rules.drl",
    ruleUnitData
  );
List<Measurement> queryResult = instance.executeQuery("FindColor").toList("$m");
instance.close();
```

# The rule

```
package example.micronaut;

unit MeasurementUnit;

rule "will execute per each Measurement having ID color"
when
  /measurements[ id == "color", $colorVal : val ]
then
  controlSet.add($colorVal);
end

query FindColor
    $m: /measurements[ id == "color" ]
end
```

# References

Getting started guides:

1. [Micronaut](https://guides.micronaut.io/latest/mn-application-aws-lambda-graalvm-gradle-java.html).
2. [Drools](https://docs.drools.org/8.39.0.Final/drools-docs/docs-website/drools/getting-started/index.html).
