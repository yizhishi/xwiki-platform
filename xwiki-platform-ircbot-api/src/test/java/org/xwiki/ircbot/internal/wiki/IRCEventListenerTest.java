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
package org.xwiki.ircbot.internal.wiki;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import org.jmock.Expectations;
import org.junit.Test;
import org.xwiki.bridge.event.DocumentCreatedEvent;
import org.xwiki.ircbot.IRCBot;
import org.xwiki.ircbot.wiki.IRCEventListenerConfiguration;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.test.AbstractMockingComponentTestCase;
import org.xwiki.test.annotation.MockingRequirement;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.web.Utils;
import com.xpn.xwiki.web.XWikiURLFactory;

/**
 * Unit tests for {@link IRCEventListener}.
 *
 * @version $Id$
 * @since 4.0M1
 */
public class IRCEventListenerTest extends AbstractMockingComponentTestCase
{
    @MockingRequirement
    private IRCEventListener listener;

    @Test
    public void onEventWhenBotNotStarted() throws Exception
    {
        final IRCBot bot = getComponentManager().lookup(IRCBot.class);

        getMockery().checking(new Expectations()
        {{
            oneOf(bot).isConnected();
            will(returnValue((false)));

            // The test is here, we verify that sendMessage is never called, i.e. that no message is sent to the IRC
            // channel. Note that these 2 statements are not needed, they're just here to make the test more explicit.
            never(bot).sendMessage(with(any(String.class)));
            never(bot).sendMessage(with(any(String.class)), with(any(String.class)));
        }});

        this.listener.onEvent(null, null, null);
    }

    @Test
    public void onEventWhenSourceIsNotAXWikiDocument() throws Exception
    {
        final IRCBot bot = getComponentManager().lookup(IRCBot.class);

        getMockery().checking(new Expectations()
        {{
            oneOf(bot).isConnected();
            will(returnValue((true)));

            // The test is here, we verify that sendMessage is never called, i.e. that no message is sent to the IRC
            // channel. Note that these 2 statements are not needed, they're just here to make the test more explicit.
            never(bot).sendMessage(with(any(String.class)));
            never(bot).sendMessage(with(any(String.class)), with(any(String.class)));
        }});

        this.listener.onEvent(null, "not a XWiki Document instance, it's a String.class", null);
    }

    @Test
    public void onEventWhenDocumentCreatedAndNotExcluded() throws Exception
    {
        final IRCBot bot = getComponentManager().lookup(IRCBot.class);
        final EntityReferenceSerializer<String> serializer =
            getComponentManager().lookup(EntityReferenceSerializer.class);
        final DocumentReference reference = new DocumentReference("wiki", "space", "page");
        final IRCEventListenerConfiguration configuration =
            getComponentManager().lookup(IRCEventListenerConfiguration.class);
        final DocumentReference userReference = new DocumentReference("userwiki", "userspace", "userpage");
        final WikiIRCModel ircModel = getComponentManager().lookup(WikiIRCModel.class);

        Utils.setComponentManager(getComponentManager());
        final XWikiContext xwikiContext = new XWikiContext();
        final XWikiURLFactory factory = getMockery().mock(XWikiURLFactory.class);
        xwikiContext.setURLFactory(factory);

        getMockery().checking(new Expectations()
        {{
            oneOf(bot).isConnected();
                will(returnValue((true)));
            oneOf(serializer).serialize(reference);
                will(returnValue("wiki:space.page"));
            oneOf(configuration).getExclusionPatterns();
                will(returnValue(Collections.emptyList()));
            oneOf(serializer).serialize(userReference);
                will(returnValue("userwiki:userspace.userpage"));
            oneOf(ircModel).getXWikiContext();
                will(returnValue(xwikiContext));
            oneOf(factory).createExternalURL("space", "page", "view", null, null, "wiki", xwikiContext);
                will(returnValue(new URL("http://someurl")));

            // The test is here!
            oneOf(bot).sendMessage("wiki:space.page was modified by userwiki:userspace.userpage (created) - "
                + "http://someurl");
        }});

        XWikiDocument document = new XWikiDocument(reference);
        document.setAuthorReference(userReference);

        this.listener.onEvent(new DocumentCreatedEvent(reference), document, null);
    }

    @Test
    public void onEventWhenDocumentCreatedButExcluded() throws Exception
    {
        final IRCBot bot = getComponentManager().lookup(IRCBot.class);
        final EntityReferenceSerializer<String> serializer =
            getComponentManager().lookup(EntityReferenceSerializer.class);
        final DocumentReference reference = new DocumentReference("wiki", "space", "page");
        final IRCEventListenerConfiguration configuration =
            getComponentManager().lookup(IRCEventListenerConfiguration.class);
        final DocumentReference userReference = new DocumentReference("userwiki", "userspace", "userpage");

        getMockery().checking(new Expectations()
        {{
            oneOf(bot).isConnected();
                will(returnValue((true)));
            oneOf(serializer).serialize(reference);
                will(returnValue("wiki:space.page"));
            oneOf(configuration).getExclusionPatterns();
                will(returnValue(Arrays.asList(Pattern.compile(".*:space\\..*"))));
            }});

        Utils.setComponentManager(getComponentManager());
        XWikiDocument document = new XWikiDocument(reference);
        document.setAuthorReference(userReference);

        this.listener.onEvent(new DocumentCreatedEvent(reference), document, null);
    }
}
