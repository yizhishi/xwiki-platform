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
package org.xwiki.flamingo.test.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.xwiki.test.docker.junit5.UITest;

/**
 * All UI tests for the Flamingo Skin. Note that XWiki is started/stopped only once during all the tests and thus they
 * must all work as a single ordered scenario.
 *
 * @version $Id$
 * @since 11.2RC1
 */
@UITest
public class AllIT
{
    @Nested
    @DisplayName("Save Edit Comments Tests")
    class NestedEditIT extends EditIT
    {
    }

    @Nested
    @DisplayName("Edit Translation Tests")
    class NestedEditTranslationIT extends EditTranslationIT
    {
    }

    @Nested
    @DisplayName("Attachment Tests")
    class NestedAttachmentIT extends AttachmentIT
    {
    }

    @Nested
    @DisplayName("Velocity Macro Tests")
    class NestedVelocityIT extends VelocityIT
    {
    }

    @Nested
    @DisplayName("Login Tests")
    class NestedLoginIT extends LoginIT
    {
    }
}
