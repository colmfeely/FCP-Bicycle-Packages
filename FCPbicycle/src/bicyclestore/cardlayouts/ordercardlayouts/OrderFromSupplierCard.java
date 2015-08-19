package bicyclestore.cardlayouts.ordercardlayouts;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import bicyclestore.Database;
import bicyclestore.bikes.BMX;
import bicyclestore.bikes.Bicycle;
import bicyclestore.bikes.Cruiser;
import bicyclestore.bikes.Hybrid;
import bicyclestore.bikes.MotorisedBike;
import bicyclestore.bikes.MountainBike;
import bicyclestore.bikes.RoadBike;
import bicyclestore.staff.Employee;
import bicyclestore.suppliers.Supplier;
import bicyclestore.transaction.PurchasingTransaction;

@SuppressWarnings("serial")
public class OrderFromSupplierCard extends JPanel {

	private Database database;
	private Employee employee;
	private OrdersCardLayout cardLayout;
	
	private JButton btnSubmit;
	
	private JLabel lblTransactionId, lblSelectSupplier, lblSelectProductType, lblSelectProductModel, lblSelectQuantity;
	private JComboBox<String> supplierList, productTypeList, productModelList;
	
	private String[] productTypes = {"Please Select Product Type", "BMX", "Mountain Bike",
			"Road Bike", "Hybrid", "Cruiser", "Motorised Bike"};
	
	private String[] productModels; // product models will be empty until use selects product type
	
	private SpinnerNumberModel numberModel;
	private JSpinner quantitySpinner;
	
	private JTextField txtId;
	private JButton btnEditId;
	private Box idPane;
	private int transactionIdCount = 10009;
	
	public OrderFromSupplierCard(Database database, Employee employee, OrdersCardLayout cardLayout) {
		this.database = database;
		this.employee = employee;
		this.cardLayout = cardLayout;
		initComponents();
		createOrderFromSupplierCard();
		
		btnSubmit.addActionListener(new ButtonListener());
	}
	
	private void initComponents() {
		btnSubmit = new JButton("Submit Order");
		
		lblTransactionId = new JLabel("Transaction ID");
		lblSelectSupplier = new JLabel("Select Supplier");
		lblSelectProductType = new JLabel("Select Product Type");
		lblSelectProductModel = new JLabel("Select Model");
		lblSelectQuantity = new JLabel("Select Quantity");
		
		setUpIdPane();
		
		// set up combo boxes
		supplierList = new JComboBox<String>(getSupplierListItems());
		productTypeList = new JComboBox<String>(productTypes);
		productTypeList.addActionListener(new ComboBoxListener());
		//productModelList = new JComboBox<String>(productModels);
		productModelList = new JComboBox<String>();
		
		// set up spinner to allow user to select quantity of items
		int minValue = 1;
		int maxValue = 1000;
		numberModel = new SpinnerNumberModel(minValue, minValue, maxValue, 1);
		quantitySpinner = new JSpinner(numberModel);
	}
	
	private void setUpIdPane() {
		idPane = Box.createHorizontalBox();
		
		// set ID field to auto-increment value, grey out colour and set editable to false
		txtId = new JTextField(transactionIdCount+"",10);
		txtId.setBackground(Color.LIGHT_GRAY);
		txtId.setEditable(false);
		
		btnEditId = new JButton("Edit ID");
		
		idPane.add(txtId);
		idPane.add(btnEditId);
		
		btnEditId.addActionListener(new ButtonListener());
	}
	
	private void createOrderFromSupplierCard() {
		TitledBorder addCustomerBorder = BorderFactory.createTitledBorder("Order from Supplier");
		addCustomerBorder.setTitleJustification(TitledBorder.CENTER);
		setBorder(addCustomerBorder);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(createOrderDetailsForm());
		add(btnSubmit);
	}
	
	private JPanel createOrderDetailsForm() {
		JPanel orderDetails = new JPanel();
		GridLayout grid = new GridLayout(5, 2);
		grid.setVgap(10);
		orderDetails.setLayout(grid);
		orderDetails.setMaximumSize(new Dimension(400, 300));
		orderDetails.add(lblTransactionId);
		orderDetails.add(idPane);
		orderDetails.add(lblSelectProductType);
		orderDetails.add(productTypeList);
		orderDetails.add(lblSelectSupplier);
		orderDetails.add(supplierList);
		orderDetails.add(lblSelectProductModel);
		orderDetails.add(productModelList);
		orderDetails.add(lblSelectQuantity);
		orderDetails.add(quantitySpinner);
		
		return orderDetails;
	}
	
	private String[] getSupplierListItems() {
		ArrayList<Supplier> suppliers = database.getSuppliers();
		String[] supplierListItems = new String[suppliers.size()];
		for(int i = 0; i < suppliers.size(); i++) {
			supplierListItems[i] = suppliers.get(i).getName();
		}
		return supplierListItems;
	}
	
	private void createPurchaseTransaction(String supplier, String productType, String model, int quantity) {
		// add purchasing transaction to the database
		int transactionId = Integer.parseInt(txtId.getText());
		// increment counter if auto-increment number was used
		if(transactionId == transactionIdCount)
			transactionIdCount++;
		double cost = database.getBicycle(model).getCostPrice();
		database.addPurhasingTransaction(new PurchasingTransaction(transactionId, employee, 
				database.getSupplier(supplier), cost, "Account", new Date()));
		
		// display confirmation dialogue to user
		JOptionPane.showMessageDialog(null, "Order processed"
				+ "\nTransaction id: "+transactionId
				+ "\nSupplier: "+supplier
				+ "\nAmount: "+cost
				+ "\nProduct type: "+productType+", Model: "+model
				+ "\nQuantity: "+quantity,
						"Order Processed", JOptionPane.INFORMATION_MESSAGE);
		
		resetFields();
		
		cardLayout.newOrderAdded(transactionId);
	}
	
	private void resetFields() {
		txtId.setText(transactionIdCount+"");
		productTypeList.setSelectedIndex(0);
		supplierList.setSelectedIndex(0);
	}
	
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			// find out if user pressed process order button
			if(event.getSource() == btnSubmit) {
				// do not attempt to save an order if no product is present in combo box
				if(productModelList.getSelectedObjects().length > 0) {
					String supplierChoice = (String)supplierList.getSelectedItem();
					String productChoice = productTypeList.getItemAt(productTypeList.getSelectedIndex());
					String model = (String)productModelList.getSelectedItem();
					int quantity = (int)quantitySpinner.getValue();
					System.out.println("Processing Order. Supplier: "+supplierChoice+", Product type: "+productChoice+", Model: "+model+", Quantity: "+quantity);
					createPurchaseTransaction(supplierChoice, productChoice, model, quantity);
				}
			}
			if(event.getSource() == btnEditId) {
				try{
					txtId.setText(Integer.parseInt(JOptionPane.showInputDialog(null, "Enter customer ID",
							"Edit Customer ID", JOptionPane.INFORMATION_MESSAGE))+"");
				}catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Customer ID must contain a number",
							"Invalid Input", JOptionPane.ERROR_MESSAGE);
				}
			}
		}	
	}
	
	private class ComboBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// populate model list combo box with bicycles of selected type
			
			// get string name for product type selected
			String productType = (String)productTypeList.getSelectedItem();
			ArrayList<String> products = new ArrayList<String>();
			
			// Fill new collection of bicycles of selected type from database
			if(productType.equals("BMX")) {
				ArrayList<Bicycle> bicycles = database.getBicycles();
				for(Bicycle bicycle : bicycles) {
					if(bicycle instanceof BMX)
						products.add(bicycle.getModel());
				}
			}
			if(productType.equals("Mountain Bike")) {
				ArrayList<Bicycle> bicycles = database.getBicycles();
				for(Bicycle bicycle : bicycles) {
					if(bicycle instanceof MountainBike)
						products.add(bicycle.getModel());
				}
			}
			if(productType.equals("Road Bike")) {
				ArrayList<Bicycle> bicycles = database.getBicycles();
				for(Bicycle bicycle : bicycles) {
					if(bicycle instanceof RoadBike)
						products.add(bicycle.getModel());
				}
			}
			if(productType.equals("Hybrid")) {
				ArrayList<Bicycle> bicycles = database.getBicycles();
				for(Bicycle bicycle : bicycles) {
					if(bicycle instanceof Hybrid)
						products.add(bicycle.getModel());
				}
			}
			if(productType.equals("Cruiser")) {
				ArrayList<Bicycle> bicycles = database.getBicycles();
				for(Bicycle bicycle : bicycles) {
					if(bicycle instanceof Cruiser)
						products.add(bicycle.getModel());
				}
			}
			if(productType.equals("Motorised Bike")) {
				ArrayList<Bicycle> bicycles = database.getBicycles();
				for(Bicycle bicycle : bicycles) {
					if(bicycle instanceof MotorisedBike)
						products.add(bicycle.getModel());
				}
			}
			
			// convert ArrayList to array and update combo box to contain selected models 
			productModels = products.toArray(new String[products.size()]);
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(productModels);
			productModelList.setModel(model);
		}
		
	}

}