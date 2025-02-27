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
package org.xwiki.test.ui;

/**
 * Execute upgrade tests.
 * 
 * @version $Id$
 */
public class Upgrade911Test extends UpgradeTest
{
    @Override
    protected void postUpdateValidate()
    {
        this.validateConsole.getLogCaptureConfiguration().registerExpected(
            // Caused by the fact that we upgrade from an old version of XWiki having these deprecated uses
            "Deprecated usage of getter [com.xpn.xwiki.api.Document.getName]"
        );
        this.validateConsole.getLogCaptureConfiguration().registerExcludes(
            "Invalid extension [org.xwiki.platform:xwiki-platform-distribution-flavor-mainwiki/9.11.8] on namespace "
                + "[wiki:xwiki] (InvalidExtensionException: Dependency [org.xwiki.platform:xwiki-platform-oldcore-"
                + "[9.11.8]] is incompatible with the core extension [org.xwiki.platform:xwiki-platform-legacy-oldcore/",
            "Failed to save job status",
            "java.nio.file.NoSuchFileException: data/jobs/status/distribution/status.xml",
            // Solr brings since 8.1.1 jetty dependencies in the classloader, so the upgrade might warn about collisions
            "org.eclipse.jetty:jetty",

            // This warning is not coming from XWiki but from one jetty dependency, apparently a configuration is not
            // properly used on Solr part. More details can be found there:
            // https://github.com/eclipse/jetty.project/issues/3454
            "No Client EndPointIdentificationAlgorithm configured for SslContextFactory"
        );
    }
}
