/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.store.legacy.doc.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.suigeneris.jrcs.rcs.Version;
import org.xwiki.environment.Environment;
import org.xwiki.environment.internal.ServletEnvironment;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.annotation.AllComponents;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.mockito.MockitoComponentManager;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiAttachmentArchive;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.web.Utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Validate {@link ListAttachmentArchive}.
 * 
 * @version $Id$
 */
@ComponentTest
@AllComponents
public class ListAttachmentArchiveTest
{
    @Test
    public void testSort()
    {
        XWikiDocument document = new XWikiDocument(new DocumentReference("wiki", "space", "page"));

        XWikiAttachment attachment11 = new XWikiAttachment(document, "file1");
        attachment11.setVersion("1.1");
        XWikiAttachment attachment12 = new XWikiAttachment(document, "file1");
        attachment12.setVersion("1.2");
        XWikiAttachment attachment31 = new XWikiAttachment(document, "file1");
        attachment31.setVersion("3.1");
        XWikiAttachment attachment41 = new XWikiAttachment(document, "file1");
        attachment41.setVersion("4.1");

        List<XWikiAttachment> attachments = new ArrayList<>();
        attachments.add(attachment41);
        attachments.add(attachment11);
        attachments.add(attachment12);
        attachments.add(attachment31);

        ListAttachmentArchive archive = new ListAttachmentArchive(attachments);

        Version[] versions = archive.getVersions();

        assertEquals("1.1", versions[0].toString());
        assertEquals("1.2", versions[1].toString());
        assertEquals("3.1", versions[2].toString());
        assertEquals("4.1", versions[3].toString());
    }

    @Test
    public void testGetArchiveAsString(MockitoComponentManager componentManager) throws Exception
    {
        Utils.setComponentManager(componentManager);
        ServletEnvironment servletEnvironment = componentManager.getInstance(Environment.class);

        ServletContext servletContextMock = mock(ServletContext.class);
        servletEnvironment.setServletContext(servletContextMock);
        when(servletContextMock.getAttribute("javax.servlet.context.tempdir"))
            .thenReturn(new File(System.getProperty("java.io.tmpdir")));

        File testDirectory = new File("target/test-" + new Date().getTime()).getAbsoluteFile();

        File temporaryDirectory = new File(testDirectory, "temporary");
        File permanentDirectory = new File(testDirectory, "permanent-dir");

        servletEnvironment.setTemporaryDirectory(temporaryDirectory);
        servletEnvironment.setPermanentDirectory(permanentDirectory);

        XWikiContext xWikiContext = new XWikiContext();
        xWikiContext.setWiki(new XWiki());
        XWikiDocument document = new XWikiDocument(new DocumentReference("wiki", "space", "page"));

        XWikiAttachment attachment11 = new XWikiAttachment(document, "file1");
        attachment11.setVersion("8.1");
        attachment11.setContent(IOUtils.toInputStream("First version", "UTF-8"));
        attachment11.setMimeType("plain/text");
        attachment11.setCharset("UTF-8");

        XWikiAttachment attachment12 = new XWikiAttachment(document, "file1");
        attachment12.setVersion("9.2");
        attachment12.setContent(IOUtils.toInputStream("Second version", "UTF-8"));
        attachment12.setMimeType("plain/text");
        attachment12.setCharset("UTF-8");

        XWikiAttachment attachment31 = new XWikiAttachment(document, "file1");
        attachment31.setVersion("12.1");
        attachment31.setContent(IOUtils.toInputStream("Third version", "UTF-8"));
        attachment31.setMimeType("plain/text");
        attachment31.setCharset("UTF-8");

        XWikiAttachment attachment41 = new XWikiAttachment(document, "file1");
        attachment41.setVersion("12.2");
        attachment41.setContent(IOUtils.toInputStream("Fourth version", "UTF-8"));
        attachment41.setMimeType("plain/text");
        attachment41.setCharset("UTF-8");

        List<XWikiAttachment> attachments = new ArrayList<>();
        attachments.add(attachment41);
        attachments.add(attachment11);
        attachments.add(attachment12);
        attachments.add(attachment31);

        ListAttachmentArchive archive = new ListAttachmentArchive(attachments);
        String archiveAsString = archive.getArchiveAsString(xWikiContext);
        assertNotNull(archiveAsString);

        assertTrue(archiveAsString.contains("8.1"));
        assertTrue(archiveAsString.contains("9.2"));
        assertTrue(archiveAsString.contains("12.1"));
        assertTrue(archiveAsString.contains("12.2"));

        XWikiAttachmentArchive xWikiAttachmentArchive = new XWikiAttachmentArchive();
        xWikiAttachmentArchive.setAttachment(new XWikiAttachment(document, "file1"));
        xWikiAttachmentArchive.setArchive(archiveAsString);
        assertArrayEquals(archive.getArchive(xWikiContext), xWikiAttachmentArchive.getArchive(xWikiContext));
    }
}
