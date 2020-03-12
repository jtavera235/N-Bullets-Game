import java.awt.Color;
import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.WorldImage;



// interface which represents a piece which is a bullet or ship
interface IPiece {
  // moves a game piece 
  IPiece move();

  // draws a game piece on the screen
  WorldImage draw();

  // gets the x coordinate of an IPiece
  int getXCoor();

  // gets the Y coordinate of an IPiece
  int getYCoor();

  // return the radius
  int getRadius();

  // represents the color of a bullet
  Color BULLETCOLOR = Color.PINK;

  // represents the color of a ship
  Color SHIPCOLOR = Color.CYAN;

  // represents the radius of a ship
  int SHIPRADIUS = 10;

  // represents the speed of the bullet
  int BULLETSPEED = 8;

  // represents the speed of the ship
  int SHIPSPEED = 4;
}

// class which contains the similar properties between a ship and bullet
abstract class APiece implements IPiece {
  int x; 
  int y;

  // constructor for an APiece
  APiece(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // returns the x coordinate of a Piece
  public int getXCoor() {
    return this.x;
  }

  // returns the y coordinate of a Piece
  public int getYCoor() {
    return this.y;
  }
}

//class which represents a Ship
class Ship extends APiece {

  boolean isMovingRight;

  // constructor for a Ship
  Ship(int x, int y, boolean isMovingRight) {
    super(x, y);
    this.isMovingRight = isMovingRight;
  }

  /*
   * fields:
   * this.x ... int
   * this.y ... int
   * this.isMovingRight ... boolean
   * 
   * methods:
   * this.move() ... IPiece
   * this.draw() ... WorldScene
   * this.getRadius() ... int
   * this.getXCoor() ... int
   * this.getYCoor() ... int
   * 
   */

  // moves the ship
  public Ship move() {
    if (this.isMovingRight) {
      return new Ship(this.x + IPiece.SHIPSPEED, this.y,  this.isMovingRight);
    }
    return new Ship(this.x - IPiece.SHIPSPEED, this.y,  this.isMovingRight);
  }

  // draw a ship
  public WorldImage draw() {
    return new CircleImage(IPiece.SHIPRADIUS, OutlineMode.SOLID, IPiece.SHIPCOLOR);
  }


  // returns ship radius
  public int getRadius() {
    return IPiece.SHIPRADIUS;
  }
}


//class which represents a Bullet
class Bullet extends APiece {

  int radius;
  int multipler;
  double xSpeed;
  double ySpeed;

  // initalizes a bullet with the passed number amount of bullets
  Bullet(int x, int y, int radius, int multiplier, double xSpeed, double ySpeed) {
    super(x, y);
    this.radius = radius;
    this.multipler = multiplier;
    this.xSpeed = xSpeed;
    this.ySpeed = ySpeed;
  }

  /*
   * fields:
   * this.x ... int
   * this.y ... int
   * this.radius ... int
   * this.multiplier ... int
   * this.xSpeed ... int
   * this.ySpeed ... int
   * 
   * methods:
   * this.move() ... IPiece
   * this.draw() ... WorldScene
   * this.getXCoor() ... int
   * this.getYCoor() ... int
   * this.getRadius() ... int
   * this.getMultiplier() ... int
   * 
   */

  // moves the bullet
  public Bullet move() {
    return new Bullet((int) (this.x + this.xSpeed), (int) (this.y + this.ySpeed),
        this.radius, this.multipler, this.xSpeed, this.ySpeed);
  }

  // draws a bullet
  public WorldImage draw() {
    return new CircleImage(this.radius, OutlineMode.SOLID, IPiece.BULLETCOLOR);
  }


  // returns the radius of the bullet
  public int getRadius() {
    return this.radius;
  }

  // returns the n value of a bullet
  public int getMultiplier() {
    return this.multipler;
  }


}
