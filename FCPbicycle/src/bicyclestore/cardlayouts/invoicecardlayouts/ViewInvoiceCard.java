package bicyclestore.cardlayouts.invoicecardlayouts;

	import java.awt.BorderLayout;
	import java.awt.Color;
	import java.awt.Dimension;
	import java.awt.GridLayout;
	import java.text.SimpleDateFormat;

	import javax.swing.BorderFactory;
	import javax.swing.DefaultListModel;
	import javax.swing.JLabel;
	import javax.swing.JList;
	import javax.swing.JPanel;
	import javax.swing.JScrollPane;
	import javax.swing.ListSelectionModel;
	import javax.swing.border.TitledBorder;
	import javax.swing.event.ListSelectionEvent;
	import javax.swing.event.ListSelectionListener;

	import bicyclestore.Database;
    import bicyclestore.transaction.SalesTransaction;

	@SuppressWarnings("serial")
	public class ViewInvoiceCard extends JPanel implements ListSelectionListener {

		private Database database;
		
		private JList<String> invoiceList;
		private DefaultListModel<String> listModel;
		private JScrollPane listScrollPane;
		private JPanel invoiceDetailsPane;
		
		private JLabel idLabel, employeeLabel, customerLabel, costLabel, paymentMethodLabel, dateLabel,
							lblTransactionID, lblEmployee, lblCustomer, lblCost, lblPaymentMethod, lblDate;
		
		public ViewInvoiceCard(Database database) {
			this.database = database;
			setUpinvoiceList();
			createViewOrderCard();
		}
		
		private void setUpinvoiceList() {
			listModel = new DefaultListModel<String>();
			addOrdersFromDB();
			
			invoiceList = new JList<String>(listModel);
			invoiceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			invoiceList.setSelectedIndex(0);
			invoiceList.addListSelectionListener(this);
			invoiceList.setVisibleRowCount(10);
			
			// set up scroll pane to contain list and wrap in titled border
			listScrollPane = new JScrollPane(invoiceList);
			TitledBorder scrollPaneBorder = BorderFactory.createTitledBorder("Select an invoice");
			scrollPaneBorder.setTitleJustification(TitledBorder.CENTER);
			listScrollPane.setBorder(scrollPaneBorder);
			
			invoiceList.setPreferredSize(new Dimension(200,200));
		}

		private void addOrdersFromDB() {
			for(SalesTransaction order : database.getSalesTransactions()) {
				listModel.addElement(order.getTransactionID()+"");
			}
		}
		
		private void createViewOrderCard() {
			setLayout(new BorderLayout());
			setUpinvoiceDetailsPane();
			setUpLabels();
			
			if(database.getCustomers().size() > 0)
				setOrderDetailsContent();
			
			add(listScrollPane, BorderLayout.WEST);
			add(invoiceDetailsPane, BorderLayout.CENTER);
		}
		
		private void setUpinvoiceDetailsPane() {
			// set titled border on panel
			GridLayout detailsGrid = new GridLayout(6,2);
			detailsGrid.setVgap(5);
			detailsGrid.setHgap(5);
			invoiceDetailsPane = new JPanel(detailsGrid);
			TitledBorder detailsBorder = BorderFactory.createTitledBorder("Invoice Details");
			detailsBorder.setTitleJustification(TitledBorder.CENTER);
			invoiceDetailsPane.setBorder(detailsBorder);
		}
		
		private void setUpLabels() {
			idLabel = new JLabel("Transaction ID:", JLabel.CENTER);
			employeeLabel = new JLabel("Employee Name:", JLabel.CENTER);
			customerLabel = new JLabel("Customer Name:", JLabel.CENTER);
			costLabel = new JLabel("Cost of transaction:", JLabel.CENTER);
			paymentMethodLabel = new JLabel("Payment method:", JLabel.CENTER);
			dateLabel = new JLabel("Date:", JLabel.CENTER);
			
			idLabel.setOpaque(true);
			idLabel.setBackground(new Color(107,106,104));
			idLabel.setForeground(Color.WHITE);
			employeeLabel.setBackground(new Color(107,106,104));
			employeeLabel.setForeground(Color.WHITE);
			employeeLabel.setOpaque(true);
			customerLabel.setBackground(new Color(107,106,104));
			customerLabel.setForeground(Color.WHITE);
			customerLabel.setOpaque(true);
			costLabel.setBackground(new Color(107,106,104));
			costLabel.setForeground(Color.WHITE);
			costLabel.setOpaque(true);
			paymentMethodLabel.setBackground(new Color(107,106,104));
			paymentMethodLabel.setForeground(Color.WHITE);
			paymentMethodLabel.setOpaque(true);
			dateLabel.setBackground(new Color(107,106,104));
			dateLabel.setForeground(Color.WHITE);
			dateLabel.setOpaque(true);
			
			lblTransactionID = new JLabel("", JLabel.CENTER);
			lblEmployee = new JLabel("", JLabel.CENTER);
			lblCustomer = new JLabel("", JLabel.CENTER);
			lblCost = new JLabel("", JLabel.CENTER);
			lblPaymentMethod = new JLabel("", JLabel.CENTER);
			lblDate = new JLabel("", JLabel.CENTER);
			lblTransactionID.setBackground(Color.LIGHT_GRAY);
			lblTransactionID.setOpaque(true);
			lblEmployee.setBackground(Color.LIGHT_GRAY);
			lblEmployee.setOpaque(true);
			lblCustomer.setBackground(Color.LIGHT_GRAY);
			lblCustomer.setOpaque(true);
			lblCost.setBackground(Color.LIGHT_GRAY);
			lblCost.setOpaque(true);
			lblPaymentMethod.setBackground(Color.LIGHT_GRAY);
			lblPaymentMethod.setOpaque(true);
			lblDate.setBackground(Color.LIGHT_GRAY);
			lblDate.setOpaque(true);
			// add labels to panel
			invoiceDetailsPane.add(idLabel);
			invoiceDetailsPane.add(lblTransactionID);
			invoiceDetailsPane.add(employeeLabel);
			invoiceDetailsPane.add(lblEmployee);
			invoiceDetailsPane.add(customerLabel);
			invoiceDetailsPane.add(lblCustomer);
			invoiceDetailsPane.add(costLabel);
			invoiceDetailsPane.add(lblCost);
			invoiceDetailsPane.add(paymentMethodLabel);
			invoiceDetailsPane.add(lblPaymentMethod);
			invoiceDetailsPane.add(dateLabel);
			invoiceDetailsPane.add(lblDate);
		}
		
		private void setOrderDetailsContent() {
			// get customer at selected index
			int transactionId = Integer.parseInt(invoiceList.getSelectedValue());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SalesTransaction order = database.getSalesTransaction(transactionId);
			lblTransactionID.setText(order.getTransactionID()+"");
			lblEmployee.setText(order.getEmployee().getName());
			lblCustomer.setText(order.getCustomer().getName());
			lblCost.setText(order.getTransactionCost()+"");
			lblPaymentMethod.setText(order.getPaymentMethod());
			lblDate.setText(sdf.format(order.getTransactionDate()));
		}
		
		private void emptyOrderDetailFields() {
			lblTransactionID.setText("");
			lblEmployee.setText("");
			lblCustomer.setText("");
			lblCost.setText("");
			lblPaymentMethod.setText("");
			lblDate.setText("");
		}
		
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			// if list still contains entries reset selection and display content
			if(!listModel.isEmpty()) {
				if(invoiceList.isSelectionEmpty())
					invoiceList.setSelectedIndex(0);
				setOrderDetailsContent();	
			}
			// if list empty reset fields to blank
			else
				emptyOrderDetailFields();
		}
		
		public void refresh(int newTransactionID) {
			listModel.addElement(newTransactionID+"");
		}
		
		public void customerDetailsEdited(int oldID, int newID) {
			listModel.setElementAt(oldID+"", listModel.indexOf(newID));
			invoiceList.setSelectedValue(newID, true);
			setOrderDetailsContent();
		}
		
		public void customerDeleted(int transactionID) {
			listModel.removeElement(transactionID);
			int currentId = Integer.parseInt(lblTransactionID.getText());
			if(currentId == transactionID) {
				invoiceList.setSelectedIndex(0);
			}
		}

	}
