import java.awt.Color;
import java.util.Random;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldEnd;
import tester.Tester;

// represents a World
class BulletWorld extends World {

  int bulletsLeft;
  int shipsDestroyed = 0;
  ILoBullets bullets;
  ILoShips ships;
  Random rand;
  int currentTick;

  // represents a Bullet World with only the bulletsLeft Constructor
  BulletWorld(int bulletsLeft) {
    this.bulletsLeft = bulletsLeft;
    this.bullets = new MtLoBullets();
    this.ships = new MtLoShips();
    this.shipsDestroyed = 0;
    this.currentTick = 0;
    this.rand = new Random();
  }

  // represents a Bullet World with all the variables passed in
  BulletWorld(int bulletsLeft, int shipsDestroyed, ILoBullets bullets,
      ILoShips ships, int currentTick) {
    this.bulletsLeft = bulletsLeft;
    this.shipsDestroyed = shipsDestroyed;
    this.bullets = bullets;
    this.ships = ships;
    this.currentTick = currentTick;
    this.rand = new Random();
  }

  // constructor for tests
  BulletWorld(int bulletsLeft, Random rand) {
    this.bulletsLeft = bulletsLeft;
    this.rand = rand;
  }

  /*
   * fields:
   * this.bulletsLeft ... int
   * this.shipsDestroyed ... int
   * this.bullets ... ILoBullets
   * this.ships ... ILoShips
   * this.rand ... Random
   * this.currentTick ... int
   * 
   * Methods:
   * makeScene() ... WorldScene
   * onKeyReleased() ... World
   * onTick() ... World
   * drawBulletsText() ... WorldScene
   * drawShipsDestroyedText() ... WorldScene
   * worldEnds() ... WorldEnd
   * 
   * methods of fields:
   *  
   * this.ships.createNewShips() ... ILoShips
   * this.ships.createNewShipsHelper() ... ILoShips
   * this.ships.addShip() ... ILoShips
   * this.ships.createRandomShip() ... Ship
   * this.ships.drawWorld() ... WorldScene
   * this.ships.moveShips() ... ILoShips
   * this.ships.filter() ... ILoShips
   * this.ships.collided() ... boolean
   * this.ships.computeDistance() ... double
   * this.ships.length() ... int
   * this.ships.lengthHelper() ... int
   * 
   * this.bullets.createNewList() ... ILoBullets
   * this.bullets.createnewListHelper() ... ILoBullets
   * this.bullets.drawWorld() ... WorldScene
   * this.bullets.moveBullets() ... ILoBullets
   * this.bullets.addBullet() ... ILoBullets
   * this.bullets.filter() ... ILoBullets
   * this.bullets.adjustCollidedBullets() ... ILoBullets
   * this.bullets.constructNewBulletList() ... ILoBullets
   * this.bullets.getDegrees() ... double
   * this.bullets.collidedWith() ... boolean
   * this.bullets.computeDistance() ... double
   * this.bullets.append() ... ILoBullets
   * this.bullets.isEmpty() ... boolean
   */

  // draws the world
  public WorldScene makeScene() {
    WorldScene scene = this.getEmptyScene();
    WorldScene worldDraw = this.bullets.drawWorld(
        this.ships.drawWorld(drawBulletsText(drawShipsDestroyedText(scene))));
    return worldDraw;
  }

  // returns the image of how many bullets are left
  WorldScene drawBulletsText(WorldScene scene) {
    return scene.placeImageXY(
        new TextImage(
            this.bulletsLeft + " Bullets left", 13, Color.BLACK), 50, 290);
  }

  // returns the image of how many ships were destroyed
  WorldScene drawShipsDestroyedText(WorldScene scene) {
    return scene.placeImageXY(
        new TextImage(
            this.shipsDestroyed + " Ships Destroyed", 13, Color.BLACK), 150, 290);
  }

  // brings a bullet into the world
  public World onKeyReleased(String key) {
    if (this.bulletsLeft == 0) {
      return this;
    }
    if (key.equals(" ")) {
      Bullet newBull = new Bullet(250, 300, 2, 1, 0,  -8);
      return new BulletWorld(this.bulletsLeft - 1,
          this.shipsDestroyed, this.bullets.addBullet(newBull),
          this.ships, this.currentTick);
    }
    return this;
  }

  // updates the world by creating new ships, updating IPieces that collided, and removing pieces
  // that have left the screen

  public World onTick() {

    int newTick = this.currentTick + 1;
    if (newTick % 28 == 0) {
      this.ships = this.ships.createNewShips(this.rand.nextInt(2) + 1, this.rand);
    }

    ILoShips filteredOutOfBoundsShips = this.ships.moveShips().filter(new ShipInScreen());
    ILoBullets filteredOutOfBoundsBullets = this.bullets.moveBullets().filter(new BulletInScreen());
    ILoBullets collidedBullets = filteredOutOfBoundsBullets.adjustCollidedBullets(
        new FilterCollidedBullets(filteredOutOfBoundsShips));
    ILoShips finalShipList = filteredOutOfBoundsShips.filter(
        new FilterCollidedShips(filteredOutOfBoundsBullets));

    return new BulletWorld(this.bulletsLeft,
        this.shipsDestroyed + (filteredOutOfBoundsShips.length() - finalShipList.length()),
        collidedBullets, finalShipList, newTick);
  }

  // ends the world program if no more bullets are left and no bullets are on the screen
  public WorldEnd worldEnds() {
    if (this.bullets.isEmpty() && this.bulletsLeft == 0) {
      return new WorldEnd(true, this.makeScene());
    }
    return new WorldEnd(false, this.makeScene());
  }
}


class RunBulletWorld {

  // CONSTANTS for BulletWorld
  double tickRate = 1.0 / 28.0;
  int screenHeight = 300;
  int screenWidth = 500;

  BulletWorld testWorld = new BulletWorld(25, new Random());

  ILoShips emptyShip = new MtLoShips();
  ILoBullets emptyBullet = new MtLoBullets();


  Bullet bullet1 = new Bullet(200, 200, 2, 1, 2, 2);
  Bullet bullet1Moved = new Bullet(202, 202, 2, 1, 2, 2);
  Bullet bullet2 = new Bullet(100, 350, 6, 3, 5, 5);
  Bullet bullet2Moved = new Bullet(105, 355, 6, 3, 5, 5);
  Bullet bullet3 = new Bullet(400, 150, 4, 2, 1, 1);

  ILoBullets listBullets1 = new ConsLoBullets(this.bullet1, this.emptyBullet);
  ILoBullets bulletList2 = new ConsLoBullets(this.bullet2, this.listBullets1);
  ILoBullets bulletOneListMoved = new ConsLoBullets(this.bullet1Moved, this.emptyBullet);
  ILoBullets bulletTwoListMoved = new ConsLoBullets(this.bullet2Moved, this.bulletOneListMoved);
  ILoBullets bulletList3 = new ConsLoBullets(this.bullet3, this.bulletList2);

  ILoBullets collidedList1 = new ConsLoBullets(new Bullet(200, 200, 4, 2, 8.0, 0), 
      this.emptyBullet);
  ILoBullets collidedList2 = new ConsLoBullets(
      new Bullet(200, 200, 4, 2, 8 * Math.cos(2 * Math.PI / 2), 8 * Math.sin(2 * Math.PI / 2)),
      this.collidedList1);
  ILoBullets collidedList3 = new ConsLoBullets(this.bullet2, this.collidedList2);

  Ship ship1 = new Ship(100,200,true);
  Ship ship1Move = new Ship(104, 200, true);
  Ship ship2 = new Ship(250, 200, false);
  Ship ship2Move = new Ship(246, 200, false);
  Ship ship3 = new Ship(200, 200, true);
  Ship ship4 = new Ship(600, 400, false);

  ILoShips ship1List = new ConsLoShips(this.ship1, this.emptyShip);
  ILoShips shipListTwo = new ConsLoShips(this.ship2, this.ship1List);
  ILoShips shipOneListMoved = new ConsLoShips(this.ship1Move, this.emptyShip);
  ILoShips shipTwoListMoved = new ConsLoShips(this.ship2Move, this.shipOneListMoved);
  ILoShips ship3List = new ConsLoShips(this.ship3, this.shipListTwo);
  ILoShips ship4List = new ConsLoShips(this.ship4, this.ship3List);


  BulletWorld world = new BulletWorld(25);
  BulletWorld world2 = new BulletWorld(0, 14, this.emptyBullet, this.ship3List, 14);
  BulletWorld world3 = new BulletWorld(5, 10, this.listBullets1, this.ship3List, 10);
  BulletWorld world4 = new BulletWorld(4, 10,
      new ConsLoBullets(new Bullet(250, 300, 2, 1, 0.0, -8.0), this.listBullets1),
      this.ship3List, 10);

  // runs the World
  boolean testWorld(Tester t) {
    return world.bigBang(this.screenWidth, this.screenHeight, tickRate);
  }



  //tests for moving a list of Bullets
  boolean testMoveBullets(Tester t) {
    return t.checkExpect(this.listBullets1.moveBullets(), this.bulletOneListMoved) 
        && t.checkExpect(this.bulletList2.moveBullets(), this.bulletTwoListMoved)
        && t.checkExpect(this.emptyBullet.moveBullets(), this.emptyBullet);
  }

  // test for adding a bullet to a list of bullets
  boolean testAddBullet(Tester t) {
    return t.checkExpect(this.listBullets1.addBullet(new Bullet(250, 200, 2, 2, 2, 2)),
        new ConsLoBullets(new Bullet(250, 200, 2, 2, 2, 2), this.listBullets1))
        && t.checkExpect(this.emptyBullet.addBullet(this.bullet1), this.listBullets1);
  }


  // test for appending two bullet list together
  boolean testAppend(Tester t) {
    return t.checkExpect(this.listBullets1.append(this.listBullets1),
        new ConsLoBullets(this.bullet1, this.listBullets1))
        && t.checkExpect(this.bulletList2.append(this.listBullets1),
            new ConsLoBullets(this.bullet2, new ConsLoBullets(this.bullet1, this.listBullets1)))
        && t.checkExpect(this.emptyBullet.append(this.bulletList2), this.bulletList2)
        && t.checkExpect(this.listBullets1.append(this.emptyBullet), this.listBullets1);
  }

  // test whether a list of bullets is empty
  boolean testIsEmpty(Tester t) {
    return t.checkExpect(new MtLoBullets().isEmpty(), true)
        && t.checkExpect(this.listBullets1.isEmpty(), false);
  }

  // test for bullet filter method
  boolean testFilter(Tester t) {
    return t.checkExpect(this.listBullets1.filter(new BulletInScreen()), this.listBullets1)
        && t.checkExpect(this.bulletList2.filter(new BulletInScreen()), this.listBullets1)
        && t.checkExpect(this.emptyBullet.filter(new BulletInScreen()), this.emptyBullet);
  }

  // test for when bullets collide
  boolean testAdjustCollidedBullets(Tester t) {
    return t.checkExpect(this.bulletList2.adjustCollidedBullets(
        new FilterCollidedBullets(this.ship3List)), this.collidedList3)
        && t.checkExpect(this.emptyBullet.adjustCollidedBullets(
            new FilterCollidedBullets(this.shipListTwo)), this.emptyBullet);
  }

  // test for constructNewBulletList (this method only gets called on a ConsLoBullets class because 
  // it's a helper method for the adjustColidedBullets method so there is no empty case
  boolean testConstructNewBulletList(Tester t) {
    return t.checkExpect(new ConsLoBullets(this.bullet1, this.emptyBullet).constructNewBulletList(
        this.bullet1, 1, new MtLoBullets()),
        this.collidedList2);
  }


  // test for getting the degrees 
  boolean testGetDegrees(Tester t) {
    return t.checkExpect(new ConsLoBullets(
        this.bullet1, this.emptyBullet).getDegrees(1, 1), 2 * Math.PI / 2)
        && t.checkExpect(new ConsLoBullets(
            this.bullet1, this.emptyBullet).getDegrees(0, 1), 0.0);
  }

  // test for whether a list of bullets collided with a specific list
  boolean testCollidedWith(Tester t) {
    return t.checkExpect(listBullets1.collidedWith(new Ship(300,300, true)), false)
        && t.checkExpect(this.listBullets1.collidedWith(new Ship(200, 200, false)), true);
  }


  // test to compute the distance formula
  boolean testComputeDistance(Tester t) {
    return t.checkExpect(new ConsLoBullets(
        this.bullet1, this.emptyBullet).computeDistance(1, 1, 1, 1), 0.0)
        && t.checkExpect(new ConsLoBullets(
            this.bullet1, this.emptyBullet).computeDistance(2, 3, 4, 5), Math.sqrt(4 + 4))
        && t.checkExpect(new ConsLoShips(
            this.ship1, this.emptyShip).computeDistance(2, 3, 4, 5), Math.sqrt(4 + 4));
  }

  // test for create ships
  boolean testCreateShips(Tester t) {
    Random rand = new Random(250);
    return t.checkExpect(this.emptyShip.createNewShips(1, rand),
        new ConsLoShips(new Ship(500, 199, false),
            new ConsLoShips(new Ship(500, 198, false), this.emptyShip)))
        && t.checkExpect(this.emptyShip.createNewShips(1, rand), 
            new ConsLoShips(new Ship(500, 125, false),
                new ConsLoShips(new Ship(0, 43, true), this.emptyShip)));
  }

  // test for create ships helper
  boolean testCreateShipsHelper(Tester t) {
    Random rand = new Random(200);
    return t.checkExpect(this.emptyShip.createNewShipsHelper(this.emptyShip, 1, rand), 
        new ConsLoShips(new Ship(500, 252, false), new ConsLoShips(new Ship(500, 235, false),
            this.emptyShip)))
        && t.checkExpect(this.ship1List.createNewShipsHelper(this.emptyShip, 1, rand), 
            new ConsLoShips(new Ship(500, 79, false), new ConsLoShips(new Ship(500, 249, false),
                this.emptyShip)));
  }

  // test for create a ship with a random y-value
  boolean testCreateRandomShip(Tester t) {
    return t.checkExpect(this.emptyShip.createRandomShip(200, 2), new Ship(0, 200, true))
        && t.checkExpect(this.ship3List.createRandomShip(100, 1), new Ship(500, 100, false));
  }

  // test to move individual ship
  boolean testMoveShip(Tester t) {
    return t.checkExpect(this.ship1.move(), this.ship1Move)
        && t.checkExpect(this.ship2.move(), this.ship2Move);
  }

  // test for moving list of ships
  boolean testMoveShipsList(Tester t) {
    return t.checkExpect(this.emptyShip.moveShips(), this.emptyShip)
        && t.checkExpect(this.ship1List.moveShips(), this.shipOneListMoved)
        && t.checkExpect(this.shipListTwo.moveShips(), this.shipTwoListMoved);
  }

  // test for filter ship 
  boolean testFilterShip(Tester t) {
    return t.checkExpect(this.emptyShip.filter(new ShipInScreen()), this.emptyShip)
        && t.checkExpect(this.ship1List.filter(new ShipInScreen()), this.ship1List)
        && t.checkExpect(this.ship3List.filter(new ShipInScreen()), this.ship3List)
        && t.checkExpect(this.ship4List.filter(new ShipInScreen()), this.ship3List)
        && t.checkExpect(this.emptyShip.filter(new FilterCollidedShips(this.bulletList2)),
            this.emptyShip)
        && t.checkExpect(this.ship3List.filter(new FilterCollidedShips(this.bulletList3)), 
            new ConsLoShips(this.ship2, new ConsLoShips(this.ship1, this.emptyShip)))
        && t.checkExpect(this.ship1List.filter(new FilterCollidedShips(this.bulletList3)),
            this.ship1List);
  }

  // test for collided
  boolean testCollided(Tester t) {
    return t.checkExpect(this.emptyShip.collided(this.bullet1), false)
        && t.checkExpect(this.ship3List.collided(this.bullet1), true)
        && t.checkExpect(this.ship1List.collided(this.bullet3), false);
  }

  // test for computing length of ship list
  boolean testLength(Tester t) {
    return t.checkExpect(this.ship3List.length(), 3)
        && t.checkExpect(this.emptyShip.length(), 0);
  }

  // test for the length helper
  boolean testLengthHelper(Tester t) {
    return t.checkExpect(this.ship4List.lengthHelper(0), 4)
        && t.checkExpect(this.emptyShip.lengthHelper(0), 0)
        && t.checkExpect(this.emptyShip.lengthHelper(2), 2);
  }

  // test draw 
  boolean testDraw(Tester t) {
    return t.checkExpect(this.bullet1.draw(),
        new CircleImage(2, OutlineMode.SOLID, IPiece.BULLETCOLOR))
        && t.checkExpect(this.ship1.draw(), 
            new CircleImage(IPiece.SHIPRADIUS, OutlineMode.SOLID, IPiece.SHIPCOLOR));
  }

  // get X coordinate
  boolean testXCoor(Tester t) {
    return t.checkExpect(this.bullet1.getXCoor(), 200)
        && t.checkExpect(this.ship1.getXCoor(), 100);
  }

  // gets the y coordinates
  boolean textYCoor(Tester t) {
    return t.checkExpect(this.bullet1.getYCoor(), 200)
        && t.checkExpect(this.ship1.getYCoor(), 200);
  }

  // test get radius
  boolean testRadius(Tester t) {
    return t.checkExpect(this.bullet1.getRadius(), 2)
        && t.checkExpect(this.ship1.getRadius(), 10);
  }

  // test drawWorld
  boolean testDrawWorld(Tester t) {
    return t.checkExpect(this.emptyShip.drawWorld(this.world.getEmptyScene()),
        this.world.getEmptyScene())
        && t.checkExpect(this.listBullets1.drawWorld(this.world.getEmptyScene()),
            this.world.getEmptyScene().placeImageXY(this.bullet1.draw(), this.bullet1.getXCoor(), 
                this.bullet1.getYCoor()))
        && t.checkExpect(this.ship1List.drawWorld(this.world.getEmptyScene()), 
            this.world.getEmptyScene().placeImageXY(this.ship1.draw(), this.ship1.getXCoor(), 
                this.ship1.getYCoor()));
  }

  // test for make scene
  boolean testMakeScene(Tester t) {
    return t.checkExpect(this.world.makeScene(),
        this.emptyBullet.drawWorld(this.emptyShip.drawWorld(
            this.world.drawBulletsText(
                this.world.drawShipsDestroyedText(this.world.getEmptyScene())))));
  }

  // test for draw bullets text
  boolean testDrawBulletsText(Tester t) {
    return t.checkExpect(this.world.drawBulletsText(this.world.getEmptyScene()),
        this.world.getEmptyScene().placeImageXY(
            new TextImage(25 + " Bullets left", 13, Color.BLACK), 50, 290));
  }

  // test for draw ships destroyed text
  boolean testShipsDestroyedText(Tester t) {
    return t.checkExpect(this.world.drawShipsDestroyedText(this.world.getEmptyScene()),
        this.world.getEmptyScene().placeImageXY(new TextImage(
            0 + " Ships Destroyed", 13, Color.BLACK), 150, 290));
  }



  // test on tick
  boolean testOnTick(Tester t) {
    return t.checkExpect(this.world.onTick(),
        new BulletWorld(this.world.bulletsLeft, this.world.shipsDestroyed, this.emptyBullet,
            this.emptyShip, 1))
        && t.checkExpect(new BulletWorld(this.world.bulletsLeft,
            this.world.shipsDestroyed,
            this.listBullets1,
            this.ship1List, 1).onTick(),
            new BulletWorld(this.world.bulletsLeft, this.world.shipsDestroyed,
                this.bulletOneListMoved, this.shipOneListMoved, 2));
  }

  // test for WorldEnd
  boolean testWorldEnd(Tester t) {
    return t.checkExpect(this.world.worldEnds(),  new WorldEnd(false, this.world.makeScene()))
        && t.checkExpect(this.world2.worldEnds(), new WorldEnd(true, this.world2.makeScene()));
  }

  // test for on key
  boolean testOnKey(Tester t) {
    return t.checkExpect(this.world.onKeyReleased(""), this.world)
        && t.checkExpect(this.world3.onKeyReleased(" "), this.world4);
  }


}





























