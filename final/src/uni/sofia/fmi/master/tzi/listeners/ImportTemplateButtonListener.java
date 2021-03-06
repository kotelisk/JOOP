package uni.sofia.fmi.master.tzi.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import uni.sofia.fmi.master.tzi.YAMLRedactor;
import uni.sofia.fmi.master.tzi.templates.ParserFactory;
import uni.sofia.fmi.master.tzi.templates.TemplateCannotBeParsed;
import uni.sofia.fmi.master.tzi.templates.TemplateParser;

public class ImportTemplateButtonListener implements ActionListener {
	private static ImportTemplateButtonListener IMPORT_TEMPLATE_LISTENER_INSTANCE = null;
	private SwingWorker<Void, Void> importTemplateWorker;
	private JFileChooser fchooser;
	private ActionEvent importEvent;
	private final String YML = ".YML";
	private final String YAML = ".YAML";
	private final String JSON = ".JSON";
	
	
	
	private ImportTemplateButtonListener() {
		fchooser = new JFileChooser();
		fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}
	
	private void initWorker(){
		importTemplateWorker = new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				System.out.println("Import template action executing in background.");
				importTemplateFile();				
				return null;
			}

			private void importTemplateFile() {
				int fcValue = fchooser.showOpenDialog((JButton)importEvent.getSource());
				if(fcValue == JFileChooser.APPROVE_OPTION){
				
					File selectedFile = fchooser.getSelectedFile();
					String ext = extensionOfFile(selectedFile);
					if(YML.equalsIgnoreCase(ext) || YAML.equalsIgnoreCase(ext) || JSON.equalsIgnoreCase(ext)){
						saveTemplateInHome(selectedFile);
						System.out.println("File " + fchooser.getSelectedFile() + " was choosen.");
					}else{
						JOptionPane.showMessageDialog(null, getErrorMessage(selectedFile.getAbsolutePath()), "Unsupported file format.", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
			private String getErrorMessage(String absolutePath) {
				return absolutePath + "is not supported.\n Supported file formats:\n YAML, YML, JSON.";
			}

			private void saveTemplateInHome(File selectedFile) {
				
				try (BufferedWriter writer = Files.newBufferedWriter(YAMLRedactor.workingDir.resolve(selectedFile.getName()), StandardCharsets.UTF_8);){
					TemplateParser tParser = ParserFactory.getParser(selectedFile);
					tParser.parseTemplate();
					writer.write(tParser.getParsedTemplate());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TemplateCannotBeParsed tcnbp) {
					tcnbp.printStackTrace();
				}
			}

			private String extensionOfFile(File selectedFile) {
				int lastDotPosition = selectedFile.getName().lastIndexOf('.');
				return selectedFile.getName().substring(lastDotPosition);
			}

			@Override
			protected void done() {
				System.out.println("Import template action done.");
				super.done();
			}
		};
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		importEvent = event;
		initWorker();
		importTemplateWorker.execute();
	}

	public static ActionListener getInstance() {
		if(IMPORT_TEMPLATE_LISTENER_INSTANCE == null){
			IMPORT_TEMPLATE_LISTENER_INSTANCE = new ImportTemplateButtonListener();
		}
		return IMPORT_TEMPLATE_LISTENER_INSTANCE;
	}
}