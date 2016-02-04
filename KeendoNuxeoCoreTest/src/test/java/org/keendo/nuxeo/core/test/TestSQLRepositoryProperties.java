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
     *
     * [{"foobar": "foobarValue", "foobar2": "foobar2Value"}] => [{"foobar2": "foobar2Value"}]
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

    /**
     *
     * {"foobar": "foobarValue", "foobar2": "foobar2Value"} => {"foobar2": "foobar2Value"}
     *
     */
    @Test
    public void resetSimpleSubPropertyOfComplex() {
        DocumentModel doc = session.createDocumentModel("/", "mydoc", "MyDocType");

        final Map<String, Serializable> complex = new HashMap<>();
        complex.put("foobar", "foobarValue");
        complex.put("foobar2", "foobar2Value");

        doc.setPropertyValue("kc:complex", (Serializable) complex);
        doc = session.createDocument(doc);
        session.save();

        complex.remove("foobar");
        doc.setPropertyValue("kc:complex", (Serializable) complex);
        doc = session.saveDocument(doc);
        session.save();

        final Map<String, Serializable> updated = (Map<String, Serializable>) doc.getPropertyValue("kc:complex");

        final String foobar2 = (String) updated.get("foobar2");
        Assert.assertEquals("foobar2Value", foobar2);
        final String foobar = (String) updated.get("foobar");
        Assert.assertTrue(StringUtils.isEmpty(foobar));
    }

    /**
     *
     * ["foo", "foo2"] => ["","foo2"]
     *
     */
    @Test
    public void updateSimpleList() {
        DocumentModel doc = session.createDocumentModel("/", "mydoc", "MyDocType");

        final String[] simpleList = { "foo", "foo2" };
        final String[] firstEltIsEmpty = { "", "foo2" };

        doc.setPropertyValue("ks:simpleList", simpleList);
        doc = session.createDocument(doc);
        session.save();

        doc.setPropertyValue("ks:simpleList", firstEltIsEmpty);
        doc = session.saveDocument(doc);
        session.save();

        final String[] updated = (String[]) doc.getPropertyValue("ks:simpleList");

        Assert.assertNotNull(updated);
        Assert.assertTrue(updated.length == 2);
        Assert.assertEquals(updated[0], "");
        Assert.assertEquals(updated[1], "foo2");
    }

    /**
     *
     * ["foo", "foo2"] => [""]
     *
     */
    @Test
    public void updateSimpleList2() {
        DocumentModel doc = session.createDocumentModel("/", "mydoc", "MyDocType");

        final String[] simpleList = { "foo" };
        final String[] firstEltIsEmpty = { "" };

        doc.setPropertyValue("ks:simpleList", simpleList);
        doc = session.createDocument(doc);
        session.save();

        doc.setPropertyValue("ks:simpleList", firstEltIsEmpty);
        doc = session.saveDocument(doc);
        session.save();

        final String[] updated = (String[]) doc.getPropertyValue("ks:simpleList");

        Assert.assertNotNull(updated);
        Assert.assertTrue(updated.length == 1);
        Assert.assertEquals(updated[0], "");
    }

    /**
     *
     * {"array": ["foo"]} => {"array": [""]}
     *
     */
    @Test
    public void updateListInsideComplex() {
        DocumentModel doc = session.createDocumentModel("/", "mydoc", "MyDocType");

        final String[] array = { "foo" };
        final String[] emptyArray = { "" };

        final Map<String, Serializable> complex = new HashMap<>();
        complex.put("array", array);

        doc.setPropertyValue("kc:complexWithArrayElt", (Serializable) complex);
        doc = session.createDocument(doc);
        session.save();

        complex.put("array", emptyArray);
        doc.setPropertyValue("kc:complexWithArrayElt", (Serializable) complex);
        doc = session.saveDocument(doc);
        session.save();

        final Map<String, Serializable> updated = (Map<String, Serializable>) doc.getPropertyValue("kc:complexWithArrayElt");

        final String[] arrayUpdated = (String[]) updated.get("array");
        Assert.assertNotNull(arrayUpdated);
        Assert.assertTrue(arrayUpdated.length == 1);
        Assert.assertEquals(arrayUpdated[0], "");
    }
}
