package jmfexample;

public class jmfexample {
    
   
    public jmfexample() {
        
        CloneableDataSource dataSource = new CloneableDataSource(null);
        dataSource.setMainSource();
        dataSource.makeDataSourceCloneable();
        dataSource.startProcessing();
        mainFrame frame = new mainFrame(dataSource);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    }
    
    
    public static void main(String[] args) {
        
        /*jmfexample jmf =*/ new jmfexample();
        
    }
    
}
