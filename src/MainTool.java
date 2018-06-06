import com.nwoods.jgo.layout.JGoTreeAutoLayout;
import groovy.lang.GroovyShell;
import groovy.lang.MissingMethodException;
import node.ErrorSubscribe;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import Setting.SettingBoxList;
import support.DialogHref;
import Setting.DialogSetting;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;


public class MainTool extends JFrame {

	private SettingBoxList settingBoxList;
	private JPanel appInfo_Panel, errorJPanel ;
	private JScrollPane treeScrollPane, actionsTree ;

	JFileChooser chooser;
	JLabel file, appName, description;

	int WIDTH = 800;
	int HEIGHT = 650;
	int HEIGHT_info = 100;


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainTool frame = new MainTool();
					frame.setVisible(true);
					frame.pack();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	public MainTool() {
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("SmartThing visualization tool");
		setBounds(100, 100, 1000, 600);

		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		JMenuItem fileItem = new JMenuItem("Open");
		fileItem.addActionListener(new OpenFileActionListener());
		menuFile.add(fileItem);
		JMenuItem fileItem2 = new JMenuItem("OpenDir");
		fileItem2.addActionListener(new OpenDirActionListener());
		menuFile.add(fileItem2);

		JMenu menuSetting = new JMenu("Setting");
		menuBar.add(menuSetting);
		JMenuItem settingItem = new JMenuItem("Open");
		settingItem.addActionListener(new OpenSettingActionListener());
		menuSetting.add(settingItem);
		JMenuItem settingItem2 = new JMenuItem("Reset");
		settingItem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileOpen();
			}
		});
		menuSetting.add(settingItem2);

		setJMenuBar(menuBar);

		JPanel mainPane = new JPanel();
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		mainPane.setLayout(new BorderLayout());
		setContentPane(mainPane);

		appInfo_Panel = new JPanel();
		JPanel actionsPanel = new JPanel();
		actionsTree= new JScrollPane();
		errorJPanel = new JPanel();
		treeScrollPane = new JScrollPane();

		appInfo_Panel.setPreferredSize(new Dimension(WIDTH, HEIGHT_info));
		appInfo_Panel.setLayout(new BoxLayout(appInfo_Panel, BoxLayout.PAGE_AXIS));
		appInfo_Panel.setBorder(new EmptyBorder(3 , 10 , 10 , 10));
		mainPane.add("North", appInfo_Panel);

		file = new JLabel();
		appName = new JLabel();
		description = new JLabel();
		JLabel textPane = new JLabel();
		textPane.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 17));
		textPane.setText("SmartApp Info");
		appInfo_Panel.add(textPane);
		appInfo_Panel.add(file);
		appInfo_Panel.add(appName);
		appInfo_Panel.add(description);

		actionsPanel.setBorder(BorderFactory.createEmptyBorder(3 , 3, 3 , 3));
		actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.PAGE_AXIS));
		actionsPanel.setPreferredSize(new Dimension(WIDTH/2-10, HEIGHT - HEIGHT_info - 20));
		actionsPanel.add(new JLabel("Counting Chart"));
		actionsPanel.add(actionsTree);
		mainPane.add("East", actionsPanel);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(3 , 3, 3 , 3));
		treeScrollPane.setBorder(BorderFactory.createEmptyBorder(3 , 3, 3 , 3));
		errorJPanel.setBorder(BorderFactory.createEmptyBorder(3 , 3, 3 , 3));
		tabbedPane.addTab("visualizing page", null, treeScrollPane, null);
		tabbedPane.addTab("subscribe error", null, errorJPanel, null);
		mainPane.add("Center", tabbedPane);

		settingBoxList = new SettingBoxList();

	}


	class OpenSettingActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			DialogSetting dialogSetting =  new DialogSetting(settingBoxList);
			settingBoxList = dialogSetting.getJDialog();
		}
	}

	class OpenDirActionListener implements ActionListener {

		public OpenDirActionListener() {

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String path="D:\\2_Master\\석사\\smartThgins_template";

			File dirFile=new File(path);
			File []fileList=dirFile.listFiles();
			CodeVisitor analysis = new CodeVisitor(settingBoxList);
			CompilerConfiguration cc = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
			cc.addCompilationCustomizers(analysis);
			GroovyShell gshell = new GroovyShell(cc);

			for(File tempFile : fileList) {

				System.out.println(tempFile.getName());
				try {
					gshell.evaluate(tempFile);
				} catch (CompilationFailedException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				} catch (MissingMethodException mme) {

				}

				analysis.getPreferenceTree();
				analysis.errorReport();
			}


		}
	}

	class OpenFileActionListener implements ActionListener {


		public OpenFileActionListener() {
			chooser = new JFileChooser();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated helper stub


			FileNameExtensionFilter filter = new FileNameExtensionFilter("파일찾기", "groovy");
			chooser.setFileFilter(filter);

			int ret = chooser.showOpenDialog(null);
			if (ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
				return;
			}

			fileOpen();
		}
	}


	private void fileOpen(){

		ArrayList error;
		HashMap definition;
		CodeVisitor analysis;
		JTree tree, actions_tree;

		treeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		analysis = new CodeVisitor(settingBoxList);
		CompilerConfiguration cc = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
		cc.addCompilationCustomizers(analysis);
		GroovyShell gshell = new GroovyShell(cc);

		File selectedFile = chooser.getSelectedFile();

		try {
			gshell.evaluate(selectedFile);
		} catch (CompilationFailedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (MissingMethodException mme) {

		}

		tree = analysis.getPreferenceTree();
		definition = analysis.getDefinition();
		error = analysis.errorReport();
		actions_tree = analysis.getActionTree();

		tree.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent me) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node != null) {
					String href = node.getUserObject().toString();
					if (href.toString().startsWith("href")) {

						TreeNode treeNode = ((DefaultMutableTreeNode) tree.getModel().getRoot()).getChildAt(2);
						int count = treeNode.getChildCount();
						for (int i = 0; i < count; i++) {
							DefaultMutableTreeNode dynamicNode = ((DefaultMutableTreeNode) (treeNode).getChildAt(i));
							String pageName = dynamicNode.getUserObject().toString().split(" ")[1];
							String hrefName = href.split(" ")[1];

							if (pageName.equals(hrefName)) {
								new DialogHref(dynamicNode, analysis.getDynamicPageList(), analysis.getSubscribeList(), analysis.getAction_methodssMap());
							}
						}
					}
				}
			}
		});

		file.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 13));
		file.setText("file : " +chooser.getSelectedFile().getName());

		if(definition.containsKey("name")) {
			appName.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 13));
			appName.setText("app name : " + definition.get("name").toString());

		}
		if(definition.containsKey("description")){
			//description.setBackground(Color.decode("#FF0000"));
			description.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 13));
			description.setText("app description : "+definition.get("description").toString());

		}

		treeScrollPane.setViewportView(tree);
		actionsTree.setViewportView(actions_tree);
		setError(error);
	}

	public void setError(ArrayList error){
		int i =0;
		ArrayList errorList = new ArrayList();

		while(i < error.size()){
			ErrorSubscribe sub = (ErrorSubscribe)error.get(i);
			JTextPane inputPane = new JTextPane();
			JTextPane capPane = new JTextPane();
			JTextPane handlerPane = new JTextPane();

			if(!sub.input) {
				appendToPane(inputPane, sub.subscribe.input, Color.RED);
				appendToPane(capPane, sub.subscribe.capability, Color.blue);
			}else {
				appendToPane(inputPane, sub.subscribe.input, Color.BLACK);
				if (!sub.capability)
					appendToPane(capPane, sub.subscribe.capability, Color.RED);
				else
					appendToPane(capPane, sub.subscribe.capability, Color.BLACK);
			}
			if(!sub.handler)
				appendToPane(handlerPane, sub.subscribe.handler, Color.RED);
			else
				appendToPane(handlerPane, sub.subscribe.handler, Color.BLACK);
			errorList.add(new JTextPaneList(inputPane, capPane, handlerPane));
			i++;
		}

		i = 0;
		errorJPanel.setBackground(Color.white);
		errorJPanel.setLayout(new GridLayout(15,5));

		while( i < errorList.size() ){
			JPanel sub = new JPanel();
			sub.setBackground(Color.white);

			JTextPaneList panleList = (JTextPaneList)errorList.get(i);

			JTextPane subOpen = new JTextPane();
			subOpen.setText("subscribe(");
			subOpen.setEditable(false);

			JTextPane subClose = new JTextPane();
			subClose.setText(")");
			subClose.setEditable(false);

			JTextPane inputPane = panleList.getinput();
			JTextPane capPane = panleList.getcap();
			JTextPane handlerPane = panleList.gethandler();

			inputPane.setEditable(false);
			capPane.setEditable(false);
			handlerPane.setEditable(false);

			sub.add(subOpen);
			sub.add(inputPane);
			sub.add(capPane);
			sub.add(handlerPane);
			sub.add(subClose);

			errorJPanel.add(sub);

			i++;
		}
	}

	class JTextPaneList {

		JTextPane inputPane;
		JTextPane capPane;
		JTextPane handlerPane;

		public JTextPaneList(JTextPane input, JTextPane cap, JTextPane handler){
			inputPane = input;
			capPane = cap;
			handlerPane = handler;
		}

		JTextPane getinput(){
			return inputPane;
		}

		JTextPane getcap(){
			return capPane;
		}

		JTextPane gethandler(){
			return handlerPane;
		}
	}

	private void appendToPane(JTextPane tp, String msg, Color c) {

		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "송조체");
		aset = sc.addAttribute(aset, StyleConstants.Alignment,StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}
}