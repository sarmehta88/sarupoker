//VideoPoker by Saru Mehta
package Kishen.Project;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.*;
import java.awt.GridLayout;

// This level has 2 decks of cards and default balance is $150
public class VideoPoker2 extends JPanel
{
  
  private List<Card> currentHand;
  private static final Decks oneDeck = new Decks(2);
  private List<Card> sortC;
  private List<Card> tempC;	
  private static final int numberOfCards=5;
 
  private boolean[] faceUp = new boolean[5]; // Array elements default is false
  

  private VideoPoker2.Display display;
  private JButton drawButton;
   private JButton showTableButton;
  private JButton dealButton;
  private JButton level3Button;
  private JButton quitButton;
  private JTextField betInput;
  private Image cardImage;
  private boolean gameInProgress;
  private String message;
  private String intro;
  private int balance = 150; //default balance
  private int bet;
  private static Image img;

  public VideoPoker2()
  {
    try
    {
      this.cardImage = ImageIO.read(getClass().getClassLoader().getResource("Kishen/cards1.png"));
       
    }
    catch (Exception e) {
      this.cardImage = null;
    }

    VideoPoker2.ButtonHandler bh = new VideoPoker2.ButtonHandler();
    VideoPoker2.MouseHandler mh = new VideoPoker2.MouseHandler();
    setLayout(new BorderLayout(5, 5));
    setBackground(new Color(100, 40, 0));
    setBorder(BorderFactory.createLineBorder(new Color(80, 100, 30), 5));
    this.display = new VideoPoker2.Display();
    this.display.addMouseListener(mh);
    add(this.display, "Center");
    JPanel bottom = new JPanel();
    add(bottom, "South");
    //Add Label Showing Bet money
    bottom.add(new JLabel("Bet:"));
    this.betInput = new JTextField("10", 6);
    bottom.add(this.betInput);

    this.showTableButton = new JButton("PAYOUT TABLE");
    this.showTableButton.addActionListener(bh);
    bottom.add(this.showTableButton);

    this.dealButton = new JButton("DEAL");
    this.dealButton.addActionListener(bh);
    bottom.add(this.dealButton);

    this.drawButton = new JButton("DRAW");
    this.drawButton.addActionListener(bh);
    bottom.add(this.drawButton);

    this.level3Button= new JButton("Level 3");
    this.level3Button.addActionListener(bh);
    bottom.add(this.level3Button);
    
    this.quitButton = new JButton("QUIT");
    this.quitButton.addActionListener(bh);
    bottom.add(this.quitButton);
    this.intro= "Need $180 to unlock level 3!!!";
    this.message = "Kishen throws in 2 Decks and more Money! Click DEAL";
    this.drawButton.setEnabled(false);
    // Level3 Button is disabled until user completes current level
    this.level3Button.setEnabled(false);
  }

  private void deal() {
    // When user has 180 in his balance, level 3 is unlocked
      if(this.balance>= 180){
          this.level3Button.setEnabled(true);
          String s1 = "You have unlocked LEVEL 3! Continue playing or explore the next level. Play Very Cautiously :O";
          JOptionPane.showMessageDialog(null,s1,"Special Unlocked Level",JOptionPane.WARNING_MESSAGE);
          
      }

    this.display.repaint();
    // Test whether user input Bet is less than Balance and is a Valid Integer
    try {
      this.bet = Integer.parseInt(this.betInput.getText().trim());
      if (this.bet > this.balance) {
        this.message = "Kishen thinks UR hella sneaky! Less money in your Piggy Bank";
        return;
      }
      if (this.bet <= 0) {
        this.message = "Don't you think the Bet should be more than zero, Duh!";
        return;
      }
    }
    catch (NumberFormatException e) {
      this.message = "Dont be a smartA$$ Enter a valid integer.";
      return;
    }

    this.gameInProgress = true;
    // Update Balance after Bet is placed
    this.balance= this.balance - this.bet; 

    // Shuffle the deck
    oneDeck.shuffle();
    // Place 5 cards from shuffled deck in the currentHand ArrayList
    try {
      currentHand = oneDeck.deal(5);
    } catch (PlayingCardException e) {
      this.intro=e.getMessage();
    }

    this.display.repaint();
    this.drawButton.setEnabled(true);
    this.dealButton.setEnabled(false);
    for (int i = 0; i < 5; i++){
      this.faceUp[i] = true;
    }
    this.message = "Click the cards to Discard & hit DRAW, goodluck dude :)";
  }


  // ReDeal the remaining cards
  private void draw() {
    this.gameInProgress = false;
    this.drawButton.setEnabled(false);
    this.dealButton.setEnabled(true);

    // Replace the face down cards with new cards from the deck
    for (int i = 0; i < 5; i++) {
      if (this.faceUp[i] == false) {
        this.faceUp[i] = true;
	try{
	tempC= oneDeck.deal(1);
	}catch(PlayingCardException e){
		System.out.println(e.getMessage());
	}
        currentHand.set(i, tempC.get(0));
	tempC.remove(0);
      }
    }
    checkHands();
    this.display.repaint();
  }
/** Check current currentHand using multipliers and goodHandTypes arrays
     *  need to sort cards from low to high
     *  print user's hand type at the end of function.
     */
    private void checkHands()
    {
        
        //create an ArrayList of sorted Cards by rank
        sortC = sortRank();
        if(isRoyalFlush()){
            this.message = ("Royal flush: Pays 500-to-1.  You win $" + 500 * this.bet);
            this.balance += 500 * this.bet;
        }else if(isStraightFlush()){
            this.message = ("Straight Flush: Pays 100-to-1.  You win $" + 100 * this.bet);
      	     this.balance += 100 * this.bet;

        }else if(isFourOfAKind()){
            this.message = ("Four-of-a-kind: Pays 50-to-1.  You win $" + 50 * this.bet);
            this.balance += 50 * this.bet;

        }else if(isFullHouse()){
            this.message = ("Full House: Pays 18-to-1.  You win $" + 18 * this.bet);
      	     this.balance += 18 * this.bet;

        }else if(isFlush()){
            this.message = ("Flush: Pays 12-to-1.  You win $" + 12 * this.bet);
            this.balance += 12 * this.bet;
        }else if( isStraight()){
            this.message = ("Straight: Pays 8-to-1.  You win $" + 8 * this.bet);
      	     this.balance += 8 * this.bet;
        }else if(isThreeKind()){
           this.message = ("Triple: Pays 6-to-1.  You win $" + 6 * this.bet);
     	    this.balance += 6 * this.bet;

        }else if(isTwoPairs()){
            this.message = ("Two Pair: Pays 4-to-1.  You win $" + 4 * this.bet);
      	     this.balance += 4 * this.bet;
        }else if( isRoyalPair()){
            DemoImage show1 = new DemoImage();
            show1.showImage();
            this.message = ("Royal Pair in the A$$: Penalty!  You lose $" + 2 * this.bet);
            this.balance -= 2 * this.bet;
        }else{
            this.message = "No hand means No Money!";
	   
      	     if (this.balance == 0)
            this.dealButton.setEnabled(false);

        }    
    }
        /*************************************************
         * private methods are called from checkhands()
         *
         *************************************************/

    private ArrayList<Card> sortRank(){
        ArrayList<Card> sortedCards= new ArrayList<Card>();
        sortedCards.add(currentHand.get(0));
        sortedCards.add(currentHand.get(1));
        sortedCards.add(currentHand.get(2));
        sortedCards.add(currentHand.get(3));
        sortedCards.add(currentHand.get(4));
        int i, j, min_j;
        
    
        for ( i = 0 ; i < sortedCards.size() ; i ++ )
        {
            
             //Find array element with min. value among
             //h[i], h[i+1], ..., h[n-1]
            min_j = i;   // Assume sortedCards.get(i) is the minimum
            
            for ( j = i+1 ; j < sortedCards.size() ; j++ )
            {
                if ( sortedCards.get(j).getRank() < sortedCards.get(min_j).getRank())
                {
                    min_j = j;    // This is a smaller rank value, update min_j
                }
            }
            
            //Swap currentHand[i] and currentHand[min_j]
             
            Card temp = sortedCards.get(i);
            sortedCards.set(i,sortedCards.get(min_j));
            sortedCards.set(min_j, temp);
        }
        return sortedCards;
    }
    
    private boolean isRoyalFlush()
        {
            if(isFlush()){
                if ((sortC.get(0)).getRank() == 1 && (sortC.get(1)).getRank() == 10 && (sortC.get(2)).getRank() == 11 			&&(sortC.get(3)).getRank() == 12 && (sortC.get(4)).getRank() == 13){
                    		return true;
                }else{
                    return false;
                }
            }
            else
                    return false;
    }
    private boolean isStraightFlush(){
            
            if(isStraight() && isFlush()){
                return true;
                
            }else{
                return false;
            }
    }
    private boolean isFlush()
        {
            for (int i = 1; i< numberOfCards; i++)
            {
                if (sortC.get(0).getSuit() != sortC.get(i).getSuit())
                {
                    return false;
                }
            }
            return true;
    }
        
    private boolean isStraight(){
        if (sortC.get(0).getRank() == 1 && sortC.get(1).getRank() == 10 && sortC.get(2).getRank() == 11 &&
            sortC.get(3).getRank() == 12 && sortC.get(4).getRank() == 13){
            return true;
        }
        for (int i = 1; i < numberOfCards; i++){
                if (sortC.get(i - 1).getRank() != (sortC.get(i).getRank() - 1))
                {
                    return false;
                }
                
        }
            return true;
    }
    private boolean isFourOfAKind(){
            //check the first 4 sorted cards to be all the same rank
            if((sortC.get(0).getRank() == sortC.get(1).getRank()) && (sortC.get(1).getRank() == sortC.get(2).getRank()) && (sortC.get(2).getRank() == sortC.get(3).getRank())){
                return true;
            }
            //check the last 4 cards to be all the same rank
            if((sortC.get(1).getRank() == sortC.get(2).getRank()) && (sortC.get(2).getRank() == sortC.get(3).getRank()) && (sortC.get(3).getRank() == sortC.get(4).getRank())){
                return true;
            }
        
            return false;
        
    }
    private boolean isFullHouse(){
            //check for xxxyy, where x and y are different ranks
            if((sortC.get(0).getRank() == sortC.get(1).getRank()) && (sortC.get(1).getRank() == sortC.get(2).getRank()) && (sortC.get(3).getRank() == sortC.get(4).getRank())){
                return true;
            }
            //check for yyxxx
            if((sortC.get(0).getRank() == sortC.get(1).getRank()) && (sortC.get(2).getRank() == sortC.get(3).getRank()) && (sortC.get(3).getRank() == sortC.get(4).getRank())){
                return true;
            }
            
            return false;
            
        }
    private boolean isThreeKind(){
            //check for xxxyz, where x,y and z are different ranks
            if((sortC.get(0).getRank() == sortC.get(1).getRank()) && (sortC.get(1).getRank() == sortC.get(2).getRank())){
                return true;
            }
            //check for yxxxz
            if((sortC.get(1).getRank() == sortC.get(2).getRank()) && (sortC.get(2).getRank() == sortC.get(3).getRank())){
                return true;
            }
            //check for yzxxx
            if((sortC.get(2).getRank() == sortC.get(3).getRank()) && (sortC.get(3).getRank() == sortC.get(4).getRank())){
                return true;
            }
            
            return false;
        
    }
    private boolean isTwoPairs(){
            //check for aabbx, where a,b and x are different ranks
            if((sortC.get(0).getRank() == sortC.get(1).getRank()) && (sortC.get(2).getRank() == sortC.get(3).getRank())){
                return true;
            }
            //check for aaxbb
            if((sortC.get(0).getRank() == sortC.get(1).getRank()) && (sortC.get(3).getRank() == sortC.get(4).getRank())){
                return true;
            }
            //check for xaabb
            if((sortC.get(1).getRank() == sortC.get(2).getRank()) && (sortC.get(3).getRank() == sortC.get(4).getRank())){
                return true;
            }
            
            return false;
            
        }
    private boolean isRoyal(int i){
        if(sortC.get(i).getRank()==1 ||sortC.get(i).getRank()==13|| sortC.get(i).getRank()==12|| sortC.get(i).getRank()==11){
            return true;
        }
        return false;
    }
    private boolean isRoyalPair(){
        
        //check for aabcd, where a,b,c and d are different ranks
        if((sortC.get(0).getRank() == sortC.get(1).getRank()) && isRoyal(0) && isRoyal(1)) {
            return true;
        }
        //check for abbcd
        if(sortC.get(1).getRank() == sortC.get(2).getRank()&& isRoyal(1) && isRoyal(2)) {
            return true;
        }
        //check for abccd
        if(sortC.get(2).getRank() == sortC.get(3).getRank()&& isRoyal(2) && isRoyal(3)) {
            return true;
        }
        // check for abcdd
        if(sortC.get(3).getRank() == sortC.get(4).getRank()&& isRoyal(3) && isRoyal(4)) {
            return true;
        }
        return false;
        
    }
  private void drawCard(Graphics g, Card card, int x, int y)
  {
    int cx;
    
    if (card.getRank() == 14)
      cx = 0;
    else
      cx = (card.getRank() - 1) * 79;
    int cy;
  
    switch (card.getSuit()) {
    case 3:
      cy = 0;
      break;
    case 2:
      cy = 123;
      break;
    case 1:
      cy = 246;
      break;
    default:
      cy = 369;
    }

    g.drawImage(this.cardImage, x, y, x + 79, y + 123, cx, cy, cx + 79, cy + 123, this);
  }

  private void drawFaceDownCard(Graphics g, int x, int y)
  {
    int cy = 492;
    int cx = 158;
    g.drawImage(this.cardImage, x, y, x + 79, y + 123, cx, cy, cx + 79, cy + 123, this);
  }
  private String showPayoutTable()
    {
        String type= "";
        int[] multipliers={-2,4,6,8,12,18,50,100,500};
        String[] goodHandTypes={
            "Royal Pair", "Two Pairs" , "Three of a Kind", "Straight", "Flush",
            "Full House", "Four of a Kind", "Straight Flush", "Royal Flush" };
        String p1=("\n\n");
        int size = multipliers.length;
        for (int i=size-1; i >= 0; i--) {
            type=type+p1+(goodHandTypes[i]+"\t|\t"+multipliers[i])+ "\n\n";
        }
        return type;
    }

// Inner Classes
  private class ButtonHandler
    implements ActionListener
  {
    private ButtonHandler()
    {
    }

    public void actionPerformed(ActionEvent evt)
    {
      Object src = evt.getSource();
      if (src == VideoPoker2.this.quitButton)
        System.exit(0);
      else if (src == VideoPoker2.this.dealButton)
        VideoPoker2.this.deal();
      else if( src== VideoPoker2.this.showTableButton){
          JFrame table= new JFrame("PAYOUT TABLE");
          JTextArea textArea=new JTextArea(showPayoutTable());
                                           
          table.setContentPane(textArea);
          table.pack();
          table.setResizable(false);
          table.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
          Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
          table.setLocation((screen.width - table.getWidth()) / 2,
                              (screen.height - table.getHeight()) / 2);
          
          table.setVisible(true);

      }else if (src == VideoPoker2.this.drawButton)
        VideoPoker2.this.draw();
      else if(src== VideoPoker2.this.level3Button){
          JFrame window3 = new JFrame("Kishen's Video Poker Level 3");
          VideoPoker3 panel3 = new VideoPoker3();
          window3.setContentPane(panel3);
          window3.pack();
          window3.setResizable(false);
          window3.setDefaultCloseOperation(3);
          Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
          window3.setLocation((screen.width - window3.getWidth()) / 2,
                              (screen.height - window3.getHeight()) / 2);
          
          window3.setVisible(true);
          
      }
    }    

  }
    private class DemoImage extends JFrame {
        public void showImage() {
            
            
            // creates the actual frame with title 'My GUI' and dimensions
            JFrame frame1 = new JFrame("Royal Pair Surprise BOOO!");
            frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame1.setSize(250,200);
            frame1.setResizable(false);
            frame1.setLocationRelativeTo(null);
            
            // Inserts the image icon
            String imgStr = "Kishen/rpa2.jpeg";
			
			
            ImageIcon image = new ImageIcon(imgStr);
            JLabel label11 = new JLabel(" ", image, JLabel.CENTER);
            frame1.getContentPane().add(label11);
            
            frame1.validate();
            frame1.setVisible(true);
            frame1.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            
        }
            
            
            
        }
  private class Display extends JPanel
  {
    Display()
    {
        // Set background image to the panel
      try{
            img= Toolkit.getDefaultToolkit().createImage("Kishen/bg2.png");
      }catch(Exception e){
            System.out.println(e.getMessage());
      }
      setOpaque(false);
      setPreferredSize(new Dimension(600, 400));
      setForeground(new Color(220, 180, 100));
      setFont(new Font("ShowCard Gothic", 1, 18));
      
    }

   public void paintComponent(Graphics g) {
       
       g.drawImage(img,0,0, getWidth(),getHeight(), this);
      super.paintComponent(g);
      for (int i = 0; i < 5; i++) {
        if (VideoPoker2.this.faceUp[i] != false)
          VideoPoker2.this.drawCard(g, VideoPoker2.this.currentHand.get(i), 20 + i * 100, 20);
        else
          VideoPoker2.this.drawFaceDownCard(g, 20 + i * 100, 20);
      }
      g.drawString(VideoPoker2.this.intro, 10, 250);
      g.drawString(VideoPoker2.this.message, 10, 300);
      g.drawString("Your PiggyBank $" + VideoPoker2.this.balance, 10, 350);
    }
  }
  private class MouseHandler extends MouseAdapter {
    private MouseHandler() {
    }
    public void mousePressed(MouseEvent evt) { if (!VideoPoker2.this.gameInProgress)
        return;
      int x = evt.getX();
      int y = evt.getY();
      for (int i = 0; i < 5; i++) {
        int cx = 20 + 100 * i;
        int cy = 20;
        if ((x > cx) && (x < cx + 79) && (y > cy) && (y < cy + 123)) {
          VideoPoker2.this.faceUp[i] = (VideoPoker2.this.faceUp[i] != false ? false : true);
          break;
        }
      }
      VideoPoker2.this.display.repaint();
    }
  }
}