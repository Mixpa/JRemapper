package me.coley.jremapper.gui.component;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import me.coley.jremapper.Program;
import me.coley.jremapper.gui.component.tree.SearchRenderer;
import me.coley.jremapper.gui.listener.SearchResultTreeListener;
import me.coley.jremapper.search.Search;

@SuppressWarnings("serial")
public class SearchPanel extends JPanel {
	private static final String SearchUTF8 = "UTF8";
	private static final String SearchClasses = "Classes";
	private static final String SearchMembers = "Members";
	private final Program callback;
	private final JTree tree = new JTree(new String[] {});
	private CardLayout layout;

	public SearchPanel(Program callback) {
		this.callback = callback;
		setLayout(new BorderLayout());
		setupSearchOptions();
		setupSearchResults();
	}

	private void setupSearchOptions() {
		JPanel wrapper = new JPanel(new BorderLayout());
		final JPanel cardController = new JPanel(layout = new CardLayout());
		JComboBox<String> combo = new JComboBox<>();
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		model.addElement(SearchUTF8);
		model.addElement(SearchClasses);
		model.addElement(SearchMembers);
		combo.setModel(model);
		combo.setEditable(false);
		combo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				layout.show(cardController, (String) evt.getItem());
			}
		});
		JPanel cardStrings = createUTFSearchPanel();
		JPanel cardClasses = createClassSearchPanel();
		JPanel cardMember = createMemberSearchPanel();
		cardController.add(cardStrings, SearchUTF8);
		cardController.add(cardClasses, SearchClasses);
		cardController.add(cardMember, SearchMembers);
		wrapper.add(combo, BorderLayout.NORTH);
		wrapper.add(cardController, BorderLayout.CENTER);
		add(wrapper, BorderLayout.NORTH);
	}

	private JPanel createUTFSearchPanel() {
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 5));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JTextField searchAll = new JTextField();
		JTextField searchStrings = new JTextField();
		JTextField searchNonStrings = new JTextField();
		p.add(new JLabel("All:"));
		p.add(searchAll);
		p.add(new JLabel("Strings:"));
		p.add(searchStrings);
		p.add(new JLabel("Non-Strings:"));
		p.add(searchNonStrings);
		//
		Function<String, DefaultMutableTreeNode> funcSAll= s -> callback.getSearcher().searchUTF8(Search.UTF_ALL, s);
		searchAll.addKeyListener(new SearchAdapter(searchAll, funcSAll));
		//
		Function<String, DefaultMutableTreeNode> funcSStrings = s -> callback.getSearcher().searchUTF8(Search.UTF_STRINGS, s);
		searchStrings.addKeyListener(new SearchAdapter(searchStrings, funcSStrings));
		//
		Function<String, DefaultMutableTreeNode> funcSNonStrings = s -> callback.getSearcher().searchUTF8(Search.UTF_NOTSTRINGS, s);
		searchNonStrings.addKeyListener(new SearchAdapter(searchNonStrings, funcSNonStrings));
		return p;
	}

	private JPanel createClassSearchPanel() {
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 5));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JTextField searchContains = new JTextField();
		JTextField searchReferencesMethods = new JTextField();
		JTextField searchReferencesFields = new JTextField();
		p.add(new JLabel("Name Contains:"));
		p.add(searchContains);
		p.add(new JLabel("References (Methods) to:"));
		p.add(searchReferencesMethods);
		p.add(new JLabel("References (Fields) to:"));
		p.add(searchReferencesFields);
		//
		Function<String, DefaultMutableTreeNode> funcSNameContains = s -> callback.getSearcher().searchClass(Search.CLASS_NAME_CONTAINS, s);
		searchContains.addKeyListener(new SearchAdapter(searchContains, funcSNameContains));
		//
		Function<String, DefaultMutableTreeNode> funcSMethodRefs = s -> callback.getSearcher().searchClass(Search.CLASS_REF_METHODS, s);
		searchReferencesMethods.addKeyListener(new SearchAdapter(searchReferencesMethods, funcSMethodRefs));
		//
		Function<String, DefaultMutableTreeNode> funcSFieldRefs = s -> callback.getSearcher().searchClass(Search.CLASS_REF_FIELDS, s);
		searchReferencesFields.addKeyListener(new SearchAdapter(searchReferencesFields, funcSFieldRefs));
		//
		return p;
	}

	private JPanel createMemberSearchPanel() {
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 5));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(new JLabel("TODO"));
		return p;
	}

	private void setupSearchResults() {
		JPanel wrapper = new JPanel(new BorderLayout());
		JScrollPane scrollTree = new JScrollPane(tree);
		wrapper.add(scrollTree, BorderLayout.CENTER);
		tree.setCellRenderer(new SearchRenderer(callback));
		SearchResultTreeListener sel = new SearchResultTreeListener(callback);
		tree.addTreeSelectionListener(sel);
		tree.addMouseListener(sel);
		add(wrapper, BorderLayout.CENTER);
	}

	public void setResults(DefaultMutableTreeNode root) {
		DefaultTreeModel model = new DefaultTreeModel(root);
		tree.setModel(model);
	}

	class SearchAdapter extends KeyAdapter {
		private final JTextField txt;
		private final Function<String, DefaultMutableTreeNode> dest;

		public SearchAdapter(JTextField txt, Function<String, DefaultMutableTreeNode> dest) {
			this.txt = txt;
			this.dest = dest;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				e.consume();
				DefaultMutableTreeNode root = dest.apply(txt.getText());
				setResults(root);
			}
		}
	}
}
