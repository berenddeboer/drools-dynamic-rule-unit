# About

Example of a [Micronaut](https://micronaut.io/) Java lambda which uses
[Drools](https://www.drools.org/). It dynamically loads .drl using a
rule unit (the modern way of writing Drools rules), and executes
it. Includes caching this to avoid recompilation to improve speed.

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
