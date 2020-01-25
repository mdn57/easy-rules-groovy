/*
 * The MIT License
 *
 * Copyright 2020 Medina Computama <medina.computama@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jalit.easy.rules.groovy;

import java.io.Reader;
import java.util.List;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.support.AbstractRuleFactory;
import org.jeasy.rules.support.JsonRuleDefinitionReader;
import org.jeasy.rules.support.RuleDefinition;
import org.jeasy.rules.support.RuleDefinitionReader;
import org.jeasy.rules.support.YamlRuleDefinitionReader;

/**
 * Factory to create {@link GroovyRule} instances.
 *
 * @author Medina Computama <medina.computama@gmail.com>
 */
public class GroovyRuleFactory extends AbstractRuleFactory<CompilerConfiguration> {

    private RuleDefinitionReader reader;

    /**
     * Create a new {@link GroovyRuleFactory} with a given reader.
     *
     * @param reader to use to read rule definitions
     * @see YamlRuleDefinitionReader
     * @see JsonRuleDefinitionReader
     */
    public GroovyRuleFactory(RuleDefinitionReader reader) {
        this.reader = reader;
    }

    /**
     * Create a new {@link GroovyRule} from a Reader.
     *
     * @param ruleDescriptor as a Reader
     * @return a new rule
     */
    public Rule createRule(Reader ruleDescriptor) throws Exception {
        return createRule(ruleDescriptor, CompilerConfiguration.DEFAULT);
    }

    /**
     * Create a new {@link GroovyRule} from a Reader.
     *
     * The rule descriptor should contain a single rule definition.
     * If no rule definitions are found, a {@link IllegalArgumentException} will be thrown.
     * If more than a rule is defined in the descriptor, the first rule will be returned.
     *
     * @param ruleDescriptor as a Reader
     * @param parserContext the Groovy parser context
     * @return a new rule
     */
    public Rule createRule(Reader ruleDescriptor, CompilerConfiguration parserContext) throws Exception {
        List<RuleDefinition> ruleDefinitions = reader.read(ruleDescriptor);
        if (ruleDefinitions.isEmpty()) {
            throw new IllegalArgumentException("rule descriptor is empty");
        }
        return createRule(ruleDefinitions.get(0), parserContext);
    }

    /**
     * Create a set of {@link GroovyRule} from a Reader.
     *
     * @param rulesDescriptor as a Reader
     * @return a set of rules
     */
    public Rules createRules(Reader rulesDescriptor) throws Exception {
        return createRules(rulesDescriptor, CompilerConfiguration.DEFAULT);
    }

    /**
     * Create a set of {@link GroovyRule} from a Reader.
     *
     * @param rulesDescriptor as a Reader
     * @return a set of rules
     */
    public Rules createRules(Reader rulesDescriptor, CompilerConfiguration parserContext) throws Exception {
        Rules rules = new Rules();
        List<RuleDefinition> ruleDefinitions = reader.read(rulesDescriptor);
        for (RuleDefinition ruleDefinition : ruleDefinitions) {
            rules.register(createRule(ruleDefinition, parserContext));
        }
        return rules;
    }

    @Override
    protected Rule createSimpleRule(RuleDefinition ruleDefinition, CompilerConfiguration parserContext) {
        GroovyRule groovyRule = new GroovyRule()
                .name(ruleDefinition.getName())
                .description(ruleDefinition.getDescription())
                .priority(ruleDefinition.getPriority())
                .when(ruleDefinition.getCondition());
        for (String action : ruleDefinition.getActions()) {
            groovyRule.then(action);
        }
        return groovyRule;
    }
    
}
