   package jmfexample;

   import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.Codec;
import javax.media.Control;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.NoProcessorException;
import javax.media.Owned;
import javax.media.Player;
import javax.media.Processor;
import javax.media.control.QualityControl;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import br.bassani.jmf.gui.AFrame;


   public class mainFrame extends AFrame {
       
       /**
	 * 
	 */
	private static final long serialVersionUID = 6228002231646281345L;

	private CloneableDataSource dataSource;
       
       private DataSource camSource;
       private DataSource recordCamSource;
       private DataSink dataSink;
       private Processor processor;
       private Processor recordProcessor;
       private PlayerStateHelper playhelper;
       
       private JFileChooser movieChooser;
       
       public mainFrame(CloneableDataSource dataSource) {
           this.dataSource = dataSource;
           this.dataSource.setParent(this);
           camSource = dataSource.cloneMainDataSource();
           initComponents();
           try{
               processor = Manager.createProcessor(camSource);
           }catch (IOException e) {
               JOptionPane.showMessageDialog(this, 
                  "Exception creating processor: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
               return;
           }catch (NoProcessorException e) {
               JOptionPane.showMessageDialog(this, 
                  "Exception creating processor: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
               return;
           }
           playhelper = new PlayerStateHelper(processor);
           if(!playhelper.configure(10000)){
               JOptionPane.showMessageDialog(this, 
                  "cannot configure processor", "Error", JOptionPane.WARNING_MESSAGE);
               return;
           }
           checkIncoding(processor.getTrackControls());
           processor.setContentDescriptor(null);
           if(!playhelper.realize(10000)){
               JOptionPane.showMessageDialog(this, 
                  "cannot realize processor", "Error", JOptionPane.WARNING_MESSAGE);
               return;
           }
           
           setJPEGQuality(processor, 1.0f);
           processor.start();
           
           processor.getVisualComponent().setBackground(Color.gray);
           centerPanel.add(processor.getVisualComponent(), BorderLayout.CENTER);
           centerPanel.add(processor.getControlPanelComponent(), BorderLayout.SOUTH);
       }
       
       
       private void initComponents() {//GEN-BEGIN:initComponents
           northPanel = new javax.swing.JPanel();
           messageLabel = new javax.swing.JLabel();
           southPanel = new javax.swing.JPanel();
           mainToolBar = new javax.swing.JToolBar();
           recordButton = new javax.swing.JButton();
           fileLabel = new javax.swing.JLabel();
           centerPanel = new javax.swing.JPanel();

           setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
           setTitle("My Webcam");
           addWindowListener(new java.awt.event.WindowAdapter() {
               public void windowClosing(java.awt.event.WindowEvent evt) {
                   formWindowClosing(evt);
               }
           });

           northPanel.setLayout(new java.awt.BorderLayout());

           messageLabel.setText("Status");
           northPanel.add(messageLabel, java.awt.BorderLayout.CENTER);

           getContentPane().add(northPanel, java.awt.BorderLayout.NORTH);

           southPanel.setLayout(new java.awt.BorderLayout());

           recordButton.setText("Record");
           recordButton.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                   recordButtonActionPerformed(evt);
               }
           });

           mainToolBar.add(recordButton);

           fileLabel.setText("File:");
           mainToolBar.add(fileLabel);

           southPanel.add(mainToolBar, java.awt.BorderLayout.CENTER);

           getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

           centerPanel.setLayout(new java.awt.BorderLayout());

           getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

           pack();
       }//GEN-END:initComponents
       
       private void formWindowClosing(java.awt.event.WindowEvent evt) {
          //GEN-FIRST:event_formWindowClosing
           processor.close();
       }//GEN-LAST:event_formWindowClosing
       
       private void recordButtonActionPerformed(java.awt.event.ActionEvent evt) {
          //GEN-FIRST:event_recordButtonActionPerformed
           if(recordButton.getText().equals("Record")){
               fileLabel.setText("File:");
               if (movieChooser == null) movieChooser = new JFileChooser();
               movieChooser.setDialogType(JFileChooser.SAVE_DIALOG);
               //Add a custom file filter and disable the default
               //(Accept All) file filter.
               movieChooser.addChoosableFileFilter(new MOVFilter());
               movieChooser.setAcceptAllFileFilterUsed(false);
               movieChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
               int returnVal = movieChooser.showDialog(this, "Record");
               if (returnVal == JFileChooser.APPROVE_OPTION) {
                   File file = movieChooser.getSelectedFile();
                   if(!file.getName().endsWith(".mov")
                      &&!file.getName().endsWith(".MOV")) file = new File(file.toString() + ".mov");
                   recordToFile(file);
                   fileLabel.setText("File:" + file.toString());
                   recordButton.setText("Stop");
               }
           }else{
               stopRecording();
               recordButton.setText("Record");
           }
       }//GEN-LAST:event_recordButtonActionPerformed
       
       void setJPEGQuality(Player p, float val) {
           Control cs[] = p.getControls();
           QualityControl qc = null;
           VideoFormat jpegFmt = new VideoFormat(VideoFormat.JPEG);
           
           // Loop through the controls to find the Quality control for
           // the JPEG encoder.
           for (int i = 0; i < cs.length; i++) {
               if (cs[i] instanceof QualityControl && cs[i] instanceof Owned) {
                   Object owner = ((Owned)cs[i]).getOwner();
                   // Check to see if the owner is a Codec.
                   // Then check for the output format.
                   if (owner instanceof Codec) {
                       Format fmts[] = ((Codec)owner).getSupportedOutputFormats(null);
                       for (int j = 0; j < fmts.length; j++) {
                           if (fmts[j].matches(jpegFmt)) {
                               qc = (QualityControl)cs[i];
                               qc.setQuality(val);
                               break;
                           }
                       }
                   }
                   if (qc != null) break;
               }
           }
       }
       
       public void checkIncoding(TrackControl track[]){
           for (int i = 0; i < track.length; i++) {
               Format format = track[i].getFormat();
               if (track[i].isEnabled() && format instanceof VideoFormat) {
                   Dimension size = ((VideoFormat)format).getSize();
                   float frameRate = ((VideoFormat)format).getFrameRate();
                   int w = (size.width % 8 == 0 ? size.width :(int)(size.width / 8) * 8);
                   int h = (size.height % 8 == 0 ? size.height :(int)(size.height / 8) * 8);
                   VideoFormat jpegFormat = new VideoFormat(
                      VideoFormat.JPEG_RTP, new Dimension(w, h), Format.NOT_SPECIFIED, Format.byteArray, frameRate);
                   messageLabel.setText("Status: Video transmitted as: " + jpegFormat.toString());
               }
           }
       }
       
       public void recordToFile(File file){
           URL movieUrl = null;
           MediaLocator dest = null;
           try{
               movieUrl = file.toURL();
               dest = new MediaLocator(movieUrl);
           }catch(MalformedURLException e){
               
           }
           
           recordCamSource = dataSource.cloneMainDataSource();
           try{
               recordProcessor = Manager.createProcessor(recordCamSource);
           }catch (IOException e) {
               JOptionPane.showMessageDialog(this, 
                  "Exception creating record processor: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
               return;
           }catch (NoProcessorException e) {
               JOptionPane.showMessageDialog(this, 
                  "Exception creating record processor: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
               return;
           }
           playhelper = new PlayerStateHelper(recordProcessor);
           if(!playhelper.configure(10000)){
               JOptionPane.showMessageDialog(this, 
                  "cannot configure record processor", "Error", JOptionPane.WARNING_MESSAGE);
               return;
           }
           
           VideoFormat vfmt = new VideoFormat(dataSource.getFormatoVideo());
           (recordProcessor.getTrackControls())[0].setFormat(vfmt);
           (recordProcessor.getTrackControls())[0].setEnabled(true);
           recordProcessor.setContentDescriptor(new FileTypeDescriptor(FileTypeDescriptor.QUICKTIME));
           Control control = recordProcessor.getControl("javax.media.control.FrameRateControl");
           if ( control != null && control instanceof javax.media.control.FrameRateControl )
              ((javax.media.control.FrameRateControl)control).setFrameRate(15.0f);
           if(!playhelper.realize(10000)){
               JOptionPane.showMessageDialog(this, 
                  "cannot realize processor", "Error", JOptionPane.WARNING_MESSAGE);
               return;
           }
           
           try {
               if(recordProcessor.getDataOutput()==null){
                   JOptionPane.showMessageDialog(this, 
                      "No Data Output", "Error", JOptionPane.WARNING_MESSAGE);
                   return;
               }
               dataSink = Manager.createDataSink(recordProcessor.getDataOutput(), dest);
               recordProcessor.start();
               dataSink.open();
               dataSink.start();
           } catch (NoDataSinkException ex) {
               JOptionPane.showMessageDialog(this, 
                  "No DataSink " + ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
           } catch (IOException ex) {
               JOptionPane.showMessageDialog(this, 
                  "IOException " + ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
           }
       }
       
       public void stopRecording(){
           try {
               recordProcessor.close();
               dataSink.stop();
               dataSink.close();
           } catch (IOException e) {
               JOptionPane.showMessageDialog(this, 
                  "cannot stop recording " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
           }
       }
       
       // Variables declaration - do not modify//GEN-BEGIN:variables
       private javax.swing.JPanel centerPanel;
       private javax.swing.JLabel fileLabel;
       private javax.swing.JToolBar mainToolBar;
       private javax.swing.JLabel messageLabel;
       private javax.swing.JPanel northPanel;
       private javax.swing.JButton recordButton;
       private javax.swing.JPanel southPanel;
       // End of variables declaration//GEN-END:variables
   }