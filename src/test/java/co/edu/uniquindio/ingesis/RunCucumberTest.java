package co.edu.uniquindio.ingesis;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@QuarkusTest
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "co.edu.uniquindio.ingesis.stepdefs")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/registro-usuario-report.html, json:target/cucumber-reports/registro-usuario-report.json, junit:target/cucumber-reports/cucumber.xml")
public class RunCucumberTest {
}


