package bicyclestore.cardlayouts.invoicecardlayouts;

	import java.awt.BorderLayout;
	import java.awt.CardLayout;
	import java.awt.Dimension;
	import java.awt.event.ItemEvent;
	import java.awt.event.ItemListener;

	import javax.swing.BorderFactory;
	import javax.swing.JComboBox;
	import javax.swing.JPanel;
	import javax.swing.border.TitledBorder;

	import bicyclestore.Database;
	import bicyclestore.cardlayouts.ordercardlayouts.ViewOrdersCard;
	import bicyclestore.staff.Employee;
	import bicyclestore.transaction.ShoppingBasket;

	@SuppressWarnings("serial")
	public class InvoiceCardLayout extends JPanel implements ItemListener {
		
		private Database database;
		private Employee employee;
		
		private JPanel  comboBoxPane, cards;
		private static final String INVOICE_A_CUSTOMER = "Sell to a Customer";
		private static final String VIEW_INVOICE = "View Invoice";
		private static final String VIEW_OLD_ORDERS = "View Old Orders";
		private static final String VIEW_DELIVERY_DATES = "View Delivery Dates";
		private static final String VIEW_COSTS = "View Costs";
		private String[] comboBoxItems = {INVOICE_A_CUSTOMER, VIEW_INVOICE};
		
		// Card classes
		private CreateInvoiceCard createInvoice;
		private InvoiceShoppingCartCard shoppingCartCard;
		private ViewInvoiceCard viewInvoiceCard;
		//private ViewOldOrdersCard viewOldOrdersCard;

		public InvoiceCardLayout(Database database, Employee employee) {
			this.database = database;
			this.employee = employee;
			createCardLayoutPane();
		}
		
		private void createCardLayoutPane() {
			// create panels which make up card layouts
			this.setLayout(new BorderLayout());
			createComboBoxPane();
			cards = new JPanel(new CardLayout());
			
			createInvoice = new CreateInvoiceCard(database, employee, this);
			viewInvoiceCard = new ViewInvoiceCard(database);
			//viewOldOrdersCard = new ViewOldOrdersCard(database);
			
			// create cards to make up card layout
			JPanel card1 = createInvoice;
			JPanel card2 = viewInvoiceCard;
			//JPanel card3 = viewOldOrdersCard;
			JPanel card4 = new JPanel();
			JPanel card5 = new JPanel();
			
			cards.add(card1, INVOICE_A_CUSTOMER);
			cards.add(card2, VIEW_INVOICE);
			//cards.add(card3, VIEW_OLD_ORDERS);
			//cards.add(card4, VIEW_DELIVERY_DATES);
			//cards.add(card5, VIEW_COSTS);
			
			this.add(comboBoxPane, BorderLayout.NORTH);
			this.add(cards, BorderLayout.CENTER);
		}
		
		private void createComboBoxPane() {
			// set up titled border for combo box pane
			TitledBorder comboBoxBorder = BorderFactory.createTitledBorder("Select Invoice option");
			comboBoxBorder.setTitleJustification(TitledBorder.CENTER);
			comboBoxPane = new JPanel();
			comboBoxPane.setPreferredSize(new Dimension(750, 65));
			comboBoxPane.setBorder(comboBoxBorder);
			
			// add items and item listener to combo box
	        JComboBox<String> cb = new JComboBox<String>(comboBoxItems);
			cb.setEditable(false);
	        cb.addItemListener(this);
	        comboBoxPane.add(cb);
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			CardLayout cl = (CardLayout)(cards.getLayout());
	        cl.show(cards, (String)e.getItem());
		}
		
		public void newOrderAdded(int transactionId) {
			// refresh customer lists
			viewInvoiceCard.refresh(transactionId);
		}

	}
