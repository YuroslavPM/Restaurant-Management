import javax.swing.*;

public class NewFrame extends JFrame {

    JPanel panelPerson = new JPanel();
    JPanel panelCar = new JPanel();
    JPanel panelRenta = new JPanel();
    JPanel sprPanel = new JPanel();

    JTabbedPane tab = new JTabbedPane();

    public NewFrame(){
        this.setSize(400,600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        tab.add(panelPerson, "Клиенти");
        tab.add(panelCar, "Коли");
        tab.add(panelRenta, "Наем");
        tab.add(sprPanel, "Справка по..");

        this.add(tab);


        this.setVisible(true);

    }



}
