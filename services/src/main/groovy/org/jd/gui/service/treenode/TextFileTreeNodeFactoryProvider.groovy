/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.service.treenode

import org.jd.gui.api.API
import org.jd.gui.api.feature.ContainerEntryGettable
import org.jd.gui.api.feature.PageCreator
import org.jd.gui.api.feature.UriGettable
import org.jd.gui.api.model.Container
import org.jd.gui.view.component.TextPage
import org.jd.gui.view.data.TreeNodeBean
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Theme
import org.fife.ui.rtextarea.Gutter

import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode

class TextFileTreeNodeFactoryProvider extends FileTreeNodeFactoryProvider {
    static final ImageIcon ICON = new ImageIcon(TextFileTreeNodeFactoryProvider.class.classLoader.getResource('org/jd/gui/images/ascii_obj.png'))

    static {
        // Early class loading
        new Gutter(new RSyntaxTextArea())
        Theme.load(TextFileTreeNodeFactoryProvider.class.classLoader.getResourceAsStream('rsyntaxtextarea/themes/eclipse.xml'))
    }

    /**
     * @return local + optional external selectors
     */
    String[] getSelectors() { ['*:file:*.txt', '*:file:*.md', '*:file:*.SF', '*:file:*.policy', '*:file:*.yaml', '*:file:*.yml'] + externalSelectors }

    public <T extends DefaultMutableTreeNode & ContainerEntryGettable & UriGettable> T make(API api, Container.Entry entry) {
        int lastSlashIndex = entry.path.lastIndexOf('/')
        def name = entry.path.substring(lastSlashIndex+1)
        return new TreeNode(entry, new TreeNodeBean(label:name, icon:ICON, tip:"Location: $entry.uri.path"))
    }

    static class TreeNode extends FileTreeNodeFactoryProvider.TreeNode implements PageCreator {
        TreeNode(Container.Entry entry, Object userObject) {
            super(entry, userObject)
        }

        public <T extends JComponent & UriGettable> T createPage(API api) {
            return new Page(entry)
        }
    }

    static class Page extends TextPage implements UriGettable {
        Container.Entry entry

        Page(Container.Entry entry) {
            this.entry = entry
            setText(entry.inputStream.text)
        }

        // --- UriGettable --- //
        URI getUri() { entry.uri }

        // --- ContentSavable --- //
        String getFileName() {
            def path = entry.path
            int index = path.lastIndexOf('/')
            return path.substring(index+1)
        }
    }
}