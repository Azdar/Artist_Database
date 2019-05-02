import java.awt.event.{ActionEvent, ActionListener, KeyAdapter, KeyEvent}
import java.awt._
import java.net.URL
import java.sql.{DriverManager, SQLException, Statement}

import javax.imageio.ImageIO
import javax.swing._
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator


object Main_Class {

  var last_activated = new Array[Boolean](3)

  def getAristList(queryDataManagment: QueryDataManagment, statement: Statement): Array[Object] = {
    val query = "SELECT DISTINCT name from capper.artist ORDER by name"
    queryDataManagment.createQuery(statement.executeQuery(query), query)
    queryDataManagment.getQuery(queryDataManagment.getSize).getObjectList
  }

  def getArtworkList(queryDataManagment: QueryDataManagment, statement: Statement): Array[Object] = {
    val query = "SELECT title from capper.artwork WHERE title NOT LIKE '%(detail)%' ORDER by title"
    queryDataManagment.createQuery(statement.executeQuery(query), query)
    queryDataManagment.getQuery(queryDataManagment.getSize).getObjectList
  }

  def getMuseumList(queryDataManagment: QueryDataManagment, statement: Statement): Array[Object] = {
    val query = "SELECT DISTINCT name from capper.location"
    queryDataManagment.createQuery(statement.executeQuery(query), query)
    queryDataManagment.getQuery(queryDataManagment.getSize).getObjectList
  }

  def getYOC(queryDataManagment: QueryDataManagment, statement: Statement): Array[Object] = {
    val query = "SELECT DISTINCT year_of_creation from capper.artwork ORDER by year_of_creation"
    queryDataManagment.createQuery(statement.executeQuery(query), query)
    queryDataManagment.getQuery(queryDataManagment.getSize).getObjectList
  }

  def updateArtworkList(queryDataManagment: QueryDataManagment, statement: Statement, artist_name: String): Array[Object] = {
    val query = "SELECT art.title from capper.artwork as art, capper.artist as artist WHERE title NOT LIKE '%(detail)%' AND artist.name = '" + artist_name + "' AND artist.id = art.artist_id ORDER by art.title"
    queryDataManagment.createQuery(statement.executeQuery(query), query)
    queryDataManagment.getQuery(queryDataManagment.getSize).getObjectList
  }

  def updateArtworkList_with_Museum(queryDataManagment: QueryDataManagment, statement: Statement, loc_name: String): Array[Object] = {
    val query = "SELECT art.title from capper.artwork as art, capper.location as loc WHERE title NOT LIKE '%(detail)%' AND loc.name = '" + loc_name + "' AND loc.id = art.museum_id ORDER by art.title"
    queryDataManagment.createQuery(statement.executeQuery(query), query)
    queryDataManagment.getQuery(queryDataManagment.getSize).getObjectList
  }

  def updateArtworkList_with_YOC(queryDataManagment: QueryDataManagment, statement: Statement, YOC: String): Array[Object] = {
    val query = "SELECT art.title from capper.artwork as art WHERE title NOT LIKE '%(detail)%' AND art.year_of_creation = " + YOC + " ORDER by art.title"
    queryDataManagment.createQuery(statement.executeQuery(query), query)
    queryDataManagment.getQuery(queryDataManagment.getSize).getObjectList
  }

  def reset(queryDataManagment: QueryDataManagment): Array[Object] ={
    println(queryDataManagment.getQuery(1).getObjectList.length)
    queryDataManagment.getQuery("SELECT title from capper.artwork WHERE title NOT LIKE '%(detail)%' ORDER by title").getObjectList
  }

  def main(args: Array[String]) {

    val qdm = new QueryDataManagment()
    //var qcd = new QueryConversionData()

    val j = new JFrame()
    j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    j.setSize(new Dimension(Toolkit.getDefaultToolkit.getScreenSize.width, Toolkit.getDefaultToolkit.getScreenSize.height))
    j.setResizable(false)
    j.setTitle("PROJECT 9- Artist/Artwork Database")

    val s = new URL("http://www.allwhitebackground.com/images/2/2278-190x190.jpg")
    val jp = new JLabel(new ImageIcon(ImageIO.read(s)))


    // load the driver

    Class.forName("org.postgresql.Driver")

    // the connect string also tells Java JDBC what driver to use,
    // magically
    val connectString = "jdbc:postgresql://flowers.mines.edu/csci403"

    // Note that readLine() and readPassword() don't play well with
    // running this code under Eclipse - may have to run on command line
    val username = "astogsdill"
    val password = "ttomme37912"

    // connect to database (must handle or throw exception)
    val db = try
      DriverManager.getConnection(connectString, username, password)
    catch {
      case e: SQLException =>
        System.out.println("Error connecting to database: " + e)
        return
    }

    // immediate queries use a Statement object - as usual you should
    // only do this for queries you write - don't build up with user
    // input - use a PreparedStatement instead.

    // Statement objects can be re-used with different queries.

    // get all statement types from the Connection object

    // must handle or throw exception for pretty much anything you
    // do in JDBC - even this seemingly innocuous code

    val immediate = try {
      db.createStatement()
    }
    catch {
      case e: SQLException =>
        System.out.print(e)
        return
    }

    //val query1 = "SELECT DISTINCT name from capper.artist"

    val ArtistList = try {
      this.getAristList(qdm, immediate)
    } catch {
      case e: SQLException =>
        e.printStackTrace()
        return
    }

    var ArtworkList = try {
      this.getArtworkList(qdm, immediate)
    } catch {
      case e: SQLException =>
        e.printStackTrace()
        return
    }

    var default_List = ArtworkList

    val MuseumList = try {
      this.getMuseumList(qdm, immediate)
    } catch {
      case e: SQLException =>
        e.printStackTrace()
        return
    }
    val YOCList = try {
      this.getYOC(qdm, immediate)
    } catch {
      case e: SQLException =>
        e.printStackTrace()
        return
    }

    var artist_name_combobox = ""
    var artist_museum_combobox = ""
    var artist_YOC_combobox = ""

    //JLabel of information to be displayed later
    val artist_name = new JLabel("Artist's Name: ")
    val artist_dob = new JLabel("Date of Birth: ")
    val artist_dod = new JLabel("Date of Death: ")
    val artist_nationality = new JLabel("Nationality: ")
    val technique = new JLabel("Technique: ")
    val artist_type = new JLabel("Type Of Artwork: ")
    val art_time_of_Creation = new JLabel("Year of Creation: ")
    val location = new JLabel("Located in: ")

    val help_button = new JButton("Help")
    help_button.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        JOptionPane.showMessageDialog(j,"Use the drop down menus to specify a specific filter and press enter to update the search results in the Artwork drop-down menu. Then you can use the" +
          "\ndrop-down menu for Artwork to select the piece you would like to view then once you have selected it press enter and your search result should display itself on the screen.")
      }
    })


    var combobox2 = new JComboBox(ArtworkList)
    //combobox2.setEditable(true)
    combobox2.setMaximumSize(new Dimension(200, 100))
    combobox2.getEditor.getEditorComponent.addKeyListener(new KeyAdapter {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_ENTER) {
          var query = "SELECT art.*, artist.name, artist.birth_year, artist.death_year, artist.nationality, loc.name, loc.city, loc.country FROM capper.artwork as art, capper.artist as artist, capper.location as loc WHERE title = '" + combobox2.getSelectedItem.toString.replace("\'", "\'\'") + "' AND artist.id = art.artist_id AND loc.id = art.museum_id"
          var index = 0
          qdm.createQuery(immediate.executeQuery(query), query)
          var result = qdm.getQuery(query).getObjectArray
          println("Result Size: " + result.size())
          if(result.size > 1){
            if(last_activated(0) || last_activated(1) || last_activated(2)){
              if(last_activated(0)){
                var artist = new Array[Object](result.size())
                for (i <- 0 until result.size()) {
                  artist(i) = result.get(i)(11).toString
                }
                for (i <- 0 until artist.length) {
                  if (artist(i).toString.equals(artist_name_combobox)) {
                    index = i
                  }
                }
              }
              if(last_activated(1)){
                val museum = new Array[Object](result.size())
                for (i <- 0 until result.size()) {
                  museum(i) = result.get(i)(15).toString
                }
                for (i <- 0 until museum.length) {
                  if (museum(i).toString.equals(artist_museum_combobox)) {
                    index = i
                  }
                }
              }
              if(last_activated(2)){
                val YOC = new Array[Object](result.size())
                for (i <- 0 until result.size()) {
                  YOC(i) = result.get(i)(10).toString
                }
                for (i <- 0 until YOC.length) {
                  if (YOC(i).toString.equals(artist_YOC_combobox)) {
                    index = i
                  }
                }
              }
            }else {
              val artist = new Array[Object](result.size())
              for (i <- 0 until result.size()) {
                artist(i) = result.get(i)(11).toString
              }
              val a = JOptionPane.showInputDialog(j, "There where multiple paintings of the same name. Please specify an artist", "Multiple Paintings Conflict", JOptionPane.PLAIN_MESSAGE, null, artist, artist(0))
              for (i <- 0 until artist.length) {
                if (artist(i).toString.equals(a)) {
                  index = i
                }
              }
            }
          }
          artist_name.setText("Artist's Name: " + result.get(index)(11).toString)
          artist_dob.setText("Date of Birth: " + result.get(index)(12).toString)
          artist_dod.setText("Date of Death: " + result.get(index)(13).toString)
          artist_nationality.setText("Nationality: " + result.get(index)(14).toString)
          technique.setText("Technique: " + result.get(index)(7).toString)
          artist_type.setText("Type of Artwork: " + result.get(index)(9).toString)
          art_time_of_Creation.setText("Year of Creation: " + result.get(index)(10).toString)
          location.setText("Location: " + result.get(index)(15))
          //println(result.get(0)(12).toString)
          var s = new URL(result.get(index)(6).toString)
          var image = ImageIO.read(s)
          var scale = 1
          while(image.getWidth / scale > jp.getWidth || image.getHeight / scale > jp.getHeight){
            scale += 1
            println(scale)
          }
          jp.setIcon(new ImageIcon(ImageIO.read(s).getScaledInstance(ImageIO.read(s).getWidth / scale, ImageIO.read(s).getHeight / scale, Image.SCALE_SMOOTH)))
        }
      }
    })
    AutoCompleteDecorator.decorate(combobox2)

    val combobox = new JComboBox(ArtistList)
    combobox.getEditor.getEditorComponent.addKeyListener(new KeyAdapter {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_ENTER) {
          last_activated(0) = true
          last_activated(1) = false
          last_activated(2) = false
          ArtworkList = updateArtworkList(qdm, immediate, combobox.getSelectedItem.toString.replace("\'", "\'\'"))
          artist_name_combobox = combobox.getSelectedItem.toString
          combobox2.removeAllItems()
          for (i <- ArtworkList)
            combobox2.addItem(i)
        }
      }
    })
    AutoCompleteDecorator.decorate(combobox)

    val combobox3 = new JComboBox(MuseumList)
    combobox3.getEditor.getEditorComponent.addKeyListener(new KeyAdapter {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_ENTER) {
          last_activated(1) = true
          last_activated(0) = false
          last_activated(2) = false
          ArtworkList = updateArtworkList_with_Museum(qdm, immediate, combobox3.getSelectedItem.toString.replace("\'", "\'\'"))
          artist_museum_combobox = combobox3.getSelectedItem.toString
          combobox2.removeAllItems();
          for (i <- ArtworkList)
            combobox2.addItem(i)
        }
      }
    })
    AutoCompleteDecorator.decorate(combobox3)

    val combobox4 = new JComboBox(YOCList)
    combobox4.getEditor.getEditorComponent.addKeyListener(new KeyAdapter {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_ENTER) {
          last_activated(2) = true
          last_activated(0) = false
          last_activated(1) = false
          ArtworkList = updateArtworkList_with_YOC(qdm, immediate, combobox4.getSelectedItem.toString)
          artist_YOC_combobox = combobox4.getSelectedItem.toString
          combobox2.removeAllItems()
          for (i <- ArtworkList) {
            combobox2.addItem(i)
          }
        }
      }
    })
    AutoCompleteDecorator.decorate(combobox4)

    val reset_button = new JButton("Reset Filter")
    reset_button.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        //ArtworkList = reset(qdm)
        last_activated(0) = false
        last_activated(1) = false
        last_activated(2) = false
        combobox2.removeAllItems()
        for (i <- default_List) {
          combobox2.addItem(i)
        }
      }
    })

    val jpanel = new JPanel()

    val buffer = new JPanel()
    val jpanel_information = new JPanel()

    val desc_artist_name = new JLabel("Artist:")
    val desc_artwork_name = new JLabel("Artwork:")
    val desc_museum_name = new JLabel("Location:")
    val desc_yoc = new JLabel("Year of Creation:")

    val gridLayout = new GridLayout(0, 1)
    gridLayout.setHgap(10)
    gridLayout.setVgap(10)
    jpanel_information.setLayout(gridLayout)
    jpanel_information.add(artist_name)
    jpanel_information.add(artist_dob)
    jpanel_information.add(artist_dod)
    jpanel_information.add(artist_nationality)
    jpanel_information.add(technique)
    jpanel_information.add(artist_type)
    jpanel_information.add(art_time_of_Creation)
    jpanel_information.add(location)

    jpanel_information.add(reset_button)

    val borderLayout = new BorderLayout()

    j.setLayout(borderLayout)
    jpanel.add(desc_artist_name)
    jpanel.add(combobox)
    jpanel.add(desc_museum_name)
    jpanel.add(combobox3)
    jpanel.add(desc_yoc)
    jpanel.add(combobox4)
    jpanel.add(desc_artwork_name)
    jpanel.add(combobox2)
    buffer.add(jpanel_information, BorderLayout.WEST)
    j.add(buffer, BorderLayout.EAST)
    j.add(jpanel, BorderLayout.NORTH)
    j.add(jp, BorderLayout.CENTER)
    j.add(help_button, BorderLayout.SOUTH)

    //j.setUndecorated(true);
    j.setVisible(true)
  }

}
