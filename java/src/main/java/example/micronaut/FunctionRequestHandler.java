package example.micronaut;

// Micronaut stuff
import io.micronaut.function.aws.MicronautRequestHandler;
import java.io.IOException;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Inject;

// Drools stuff
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnit;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.ruleunits.impl.InternalRuleUnit;
import org.drools.model.codegen.execmodel.CanonicalModelKieProject;
import java.util.ServiceLoader;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.drools.ruleunits.api.RuleUnitData;

public class FunctionRequestHandler extends MicronautRequestHandler<Measurement, Measurement> {
    @Inject
    JsonMapper objectMapper;

    Map<String, RuleUnit<RuleUnitData>> ruleUnits = new HashMap<>();

    @Override
    public Measurement execute(Measurement input) {
        System.out.println("Micronaut version: " + io.micronaut.core.version.VersionUtils.MICRONAUT_VERSION);
        try {
            System.out.println("input: " + objectMapper.writeValueAsString(input));
        } catch (IOException e) {
            throw new RuntimeException("Error processing input", e);
        }

        MeasurementUnit ruleUnitData = new MeasurementUnit();
        ruleUnitData.getMeasurements().add(input);

        RuleUnitInstance<MeasurementUnit> instance = (RuleUnitInstance<MeasurementUnit>) newInstance("src/main/resources/example/micronaut/rules.drl", ruleUnitData);
        List<Measurement> queryResult = instance.executeQuery("FindColor").toList("$m");
        instance.close();
        try {
            System.out.println("output: " + objectMapper.writeValueAsString(queryResult));
        } catch (IOException e) {
            throw new RuntimeException("Error processing output", e);
        }
        return queryResult.get(0);
    }

    public RuleUnitInstance<? extends RuleUnitData> newInstance(String path, RuleUnitData ruleUnitData) {
        RuleUnit<RuleUnitData> ruleUnit = (RuleUnit<RuleUnitData>) getRuleUnit(path, ruleUnitData);
        RuleUnitInstance<RuleUnitData> instance = ruleUnit.createInstance(ruleUnitData);
        return instance;
    }

    public RuleUnit<? extends RuleUnitData> getRuleUnit(String path, RuleUnitData ruleUnitData) {
        String ruleUnitName = ruleUnitData.getClass().getCanonicalName();
        RuleUnit<RuleUnitData> ruleUnit = ruleUnits.get(ruleUnitName);
        if (ruleUnit == null) {
            System.out.println("Creating new rule unit for " + ruleUnitName);
            ruleUnit = (RuleUnit<RuleUnitData>) newRuleUnit(path, ruleUnitData);
        }
        else {
            System.out.println("Found existing rule unit for " + ruleUnitName);
        }
        return ruleUnit;
    }

    public RuleUnit<? extends RuleUnitData> newRuleUnit(String path, RuleUnitData ruleUnitData) {
        KieModuleKieProject kieModuleKieProject = compileDrl(path);

        ClassLoader classLoader = kieModuleKieProject.getClassLoader();
        ServiceLoader<RuleUnit> loader = ServiceLoader.load(RuleUnit.class, classLoader);
        for (RuleUnit<RuleUnitData> impl : loader) {
            String ruleUnitName = ((InternalRuleUnit<RuleUnitData>) impl).getRuleUnitDataClass().getCanonicalName();
            ruleUnits.put( ruleUnitName, impl);
        }
        String ruleUnitName = ruleUnitData.getClass().getCanonicalName();
        RuleUnit<RuleUnitData> ruleUnit = ruleUnits.get(ruleUnitName);

        return ruleUnit;
    }

    public KieModuleKieProject compileDrl(String path) {
        KieServices ks = KieServices.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            // Somehow this path is important
            kfs.write("src/main/resources/example/micronaut/test.drl", content);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
        // Compile via kie internals
        InternalKieModule kieModule = (InternalKieModule) ks.newKieBuilder( kfs )
            .getKieModule(ExecutableModelProject.class);

        KieModuleKieProject kieModuleKieProject = new CanonicalModelKieProject(kieModule, kieModule.getModuleClassLoader());
        return kieModuleKieProject;
    }

}
