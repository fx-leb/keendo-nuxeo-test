package org.keendo.nuxeo.core.test;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;


@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@LocalDeploy({ "org.keendo.nuxeo.core.test.tests:OSGI-INF/test-repo-core-types-contrib.xml" })
public class TestSQLRepositoryProperties {

    @Inject
    protected CoreSession session;

    DocumentModel         doc;

    /**
     * Checks reset of a simple element of complex list element.
     *
     * [{"foo": "bar", "foo2": "bar2"}] => [{"foo": "bar", "foo2": "bar2"}]
     *
     */
    @Test
    public void resetSimpleSubPropertyOfComplexList() {
        DocumentModel doc = session.createDocumentModel("/", "mydoc", "MyDocType");

        final List<Map<String, Serializable>> complexList = new ArrayList<>();
        final Map<String, Serializable> complex = new HashMap<>();
        complexList.add(complex);
        complex.put("foobar", "foobarValue");
        complex.put("foobar2", "foobar2Value");

        doc.setPropertyValue("kcl:complexList", (Serializable) complexList);
        doc = session.createDocument(doc);
        session.save();

        complex.remove("foobar");
        doc.setPropertyValue("kcl:complexList", (Serializable) complexList);
        doc = session.saveDocument(doc);
        session.save();

        final List<Map<String, Serializable>> updatedComplexList = (List<Map<String, Serializable>>) doc.getPropertyValue("kcl:complexList");

        final String foobar2 = (String) updatedComplexList.get(0).get("foobar2");
        Assert.assertEquals("foobar2Value", foobar2);
        final String foobar = (String) updatedComplexList.get(0).get("foobar");
        Assert.assertTrue(StringUtils.isEmpty(foobar));
    }
}
