// LIST.JAVA
import java.util.Random;
import javalib.funworld.WorldScene;


//represents a list of bullets
interface ILoBullets {


  // draws the list of bullets
  WorldScene drawWorld(WorldScene scene);

  // moves the list of ships
  ILoBullets moveBullets();

  // add a bullet to the list
  ILoBullets addBullet(Bullet bull);

  // filters out the list of bullets that are no longer on the screen
  ILoBullets filter(IPred<Bullet> pred);

  // adjust the list of collided bullets
  ILoBullets adjustCollidedBullets(IPred<Bullet> pred);

  // determines if the passed ship collided with any of the bullets from the list
  boolean collidedWith(Ship s);

  // appends the passed list to the current list
  ILoBullets append(ILoBullets list);

  // determines if a list of bullets is empty
  boolean isEmpty();

}


// represents a list of bullets
class ConsLoBullets implements ILoBullets {
  Bullet first;
  ILoBullets rest;

  // initializes a list of bullets
  ConsLoBullets(Bullet first, ILoBullets rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * fields:
   * this.first ... Bullet
   * this.rest ... ILoBullets
   * 
   * methods:
   * createNewList() ... ILoBullets
   * createnewListHelper() ... ILoBullets
   * drawWorld() ... WorldScene
   * moveBullets() ... ILoBullets
   * addBullet() ... ILoBullets
   * filter() ... ILoBullets
   * adjustCollidedBullets() ... ILoBullets
   * constructNewBulletList() ... ILoBullets
   * getDegrees() ... double
   * collidedWith() ... boolean
   * computeDistance() ... double
   * append() ... ILoBullets
   * isEmpty() ... boolean
   * 
   * methods for fields:
   * this.rest.drawWorld()... WorldScene
   * this.rest.moveBullets() ... ILoBullets
   * this.rest.addBullet() ... ILoBullets
   * this.rest.filter() ... ILoBullets
   * this.rest.adjustCollidedBullets() ... ILoBullets
   * this.rest.collidedWith() ... boolean
   * this.rest.append() ... ILoBullets
   * this.rest.isEmpty() ... boolean
   */


  // draws the list of bullets
  public WorldScene drawWorld(WorldScene scene) {
    return this.rest.drawWorld(scene).placeImageXY(
        this.first.draw(), this.first.getXCoor(), this.first.getYCoor());
  }

  // moves all the ships in the list
  public ILoBullets moveBullets() {
    return new ConsLoBullets(this.first.move(), this.rest.moveBullets());
  }

  // adds a bullet to the list
  public ILoBullets addBullet(Bullet bull) {
    return new ConsLoBullets(bull, this);
  }


  // filter the list to remove any elements that are outside the screen
  public ILoBullets filter(IPred<Bullet> pred) {
    if (pred.apply(this.first)) {
      return this.rest.filter(pred);
    } else {
      return new ConsLoBullets(this.first, this.rest.filter(pred));
    }
  }

  // returns the adjusted list of bullets
  public ILoBullets adjustCollidedBullets(IPred<Bullet> pred) {
    if (pred.apply(this.first)) {
      ILoBullets updatedList = constructNewBulletList(this.first, this.first.getMultiplier(),
          new MtLoBullets());
      return this.rest.adjustCollidedBullets(pred).append(updatedList);
    } else {
      return new ConsLoBullets(this.first, this.rest.adjustCollidedBullets(pred));
    }
  }

  // constructs a new bullet list with the correct bullet measurements after a bullet collides
  ILoBullets constructNewBulletList(Bullet t, int count, ILoBullets list) {
    int radius = 0;
    if (t.getRadius() > 10) {
      radius = 10;
    } else {
      radius = t.getRadius() + 2;
    }
    if (count == 0) {
      Bullet nb = new Bullet(t.getXCoor(), t.getYCoor(), radius, t.getMultiplier() + 1,
          Math.cos(getDegrees(count, t.getMultiplier())) * IPiece.BULLETSPEED,
          IPiece.BULLETSPEED * Math.sin(getDegrees(count, t.getMultiplier())));
      return new ConsLoBullets(nb, list);
    }
    Bullet nb = new Bullet(t.getXCoor(), t.getYCoor(), radius,  t.getMultiplier() + 1,
        Math.cos(getDegrees(count, t.getMultiplier())) * IPiece.BULLETSPEED,
        IPiece.BULLETSPEED * Math.sin(getDegrees(count, t.getMultiplier())));
    return new ConsLoBullets(nb, constructNewBulletList(t, count - 1, list));
  }

  // returns the degrees for this bullet to shoot out of
  double getDegrees(int i, int m) {
    return i * 2 * Math.PI / (m + 1);
  }

  // determines if the passed bullet collided with any of the Ships
  public boolean collidedWith(Ship s) {
    return this.computeDistance(
        this.first.getXCoor(), this.first.getYCoor(), s.getXCoor(), s.getYCoor())
        <= this.first.getRadius()
        || this.computeDistance(
            this.first.getXCoor(), this.first.getYCoor(), s.getXCoor(), s.getYCoor())
        <= s.getRadius()
        || this.rest.collidedWith(s);
  }

  // computes the distance formula
  double computeDistance(int x1, int y1, int x2, int y2) {
    return Math.sqrt( ((double) x2 - (double) x1) * ((double) x2 - (double) x1)
        + ((double) y2 - (double) y1) * ((double) y2 - (double) y1));
  }

  // appends the passed list to this list
  public ILoBullets append(ILoBullets list) {
    return new ConsLoBullets(this.first, this.rest.append(list));
  }

  // this is not an empty list so it returns false
  public boolean isEmpty() {
    return false;
  }


}


// represents an empty list of bullets
class MtLoBullets implements ILoBullets {

  // constructor for empty list of bullets
  MtLoBullets() {
  }

  /*
   * methods:
   * createNewList() ... ILoBullets
   * createnewListHelper() ... ILoBullets
   * drawWorld() ... WorldScene
   * moveBullets() ... ILoBullets
   * addBullet() ... ILoBullets
   * filter() ... ILoBullets
   * adjustCollidedBullets() ... ILoBullets
   * constructNewBulletList() ... ILoBullets
   * getDegrees() ... double
   * collidedWith() ... boolean
   * computeDistance() ... double
   * append() ... ILoBullets
   * isEmpty() ... boolean
   */

  // returns the passed empty scene
  public WorldScene drawWorld(WorldScene scene) {
    return scene;
  }

  // cannot move empty list of bullets
  public ILoBullets moveBullets() {
    return this;
  }

  // adds a bullet to the empty list
  public ILoBullets addBullet(Bullet bull) {
    return new ConsLoBullets(bull, this);
  }

  // returns empty list of bullets
  public ILoBullets filter(IPred<Bullet> pred) {
    return this;
  }


  // no ship can collide with an empty list of bullets
  public boolean collidedWith(Ship s) {
    return false;
  }

  // returns the passed list
  public ILoBullets append(ILoBullets list) {
    return list;
  }


  // returns an empty list of bullets
  public ILoBullets adjustCollidedBullets(IPred<Bullet> pred) {
    // TODO Auto-generated method stub
    return new MtLoBullets();
  }

  // this is an empty list so it returns true
  public boolean isEmpty() {
    return true;
  }


}


//represents a list of bullets
interface ILoShips {
  // creates new ships that span
  ILoShips createNewShips(int max, Random rand);

  // helper for the create new ships 
  ILoShips createNewShipsHelper(ILoShips originalList, int max, Random rand);

  // creates a random ship
  Ship createRandomShip(int y, int randomSide);

  // removes the ships that are outside the range
  ILoShips filter(IPred<Ship> pred);

  // draws the list of ships
  WorldScene drawWorld(WorldScene scene);

  // moves all the ships
  ILoShips moveShips();

  // determines if a bullet collided with any of the ships
  boolean collided(Bullet b);

  // computes length of the list
  int length();

  // helper method for the length of a list
  int lengthHelper(int start);
}


// represents a list of Ships
class ConsLoShips implements ILoShips {
  Ship first;
  ILoShips rest;

  // Constructor for a List of Ships
  ConsLoShips(Ship first, ILoShips rest) {
    this.first = first;
    this.rest = rest;
  }


  /*
   * fields:
   * this.first ... Ship
   * this.rest ... ILoShip
   * this.rand ... Random
   * 
   * methods:
   * createNewShips() ... ILoShips
   * createNewShipsHelper() ... ILoShips
   * createRandomShip() ... Ship
   * drawWorld() ... WorldScene
   * moveShips() ... ILoShips
   * filter() ... ILoShips
   * collided() ... boolean
   * computeDistance() ... double
   * length() ... int
   * lengthHelper() ... int
   * 
   * methods for fields:
   * this.rest.createNewShips() ... ILoShips
   * this.rest.createNewShipsHelper() ... ILoShips
   * this.rest.createRandomShip() ... Ship
   * this.rest.drawWorld() ... WorldScene
   * this.rest.moveShips() ... ILoShips
   * this.rest.filter() ... ILoShips
   * this.rest.collided() ... boolean
   * this.rest.computeDistance() ... double
   * this.rest.length() ... int
   * this.rest.lengthHelper() ... int
   * 
   */

  // adds new ships to the list spawn
  public ILoShips createNewShips(int max, Random rand) {
    return createNewShipsHelper(this, max, rand);
  }


  // helper for the create ships method
  public ILoShips createNewShipsHelper(ILoShips originalList, int max, Random rand) {
    int limit = rand.nextInt(214) + 43;
    if (max == 0) {
      return new ConsLoShips(createRandomShip(limit, rand.nextInt(2)), originalList);
    }
    return new ConsLoShips(createRandomShip(limit, rand.nextInt(2)),
        this.createNewShipsHelper(originalList, max - 1, rand));
  }

  //test for create a ship with a random y-value
  public Ship createRandomShip(int y, int randomSide) {

    if (randomSide % 2 == 0) {
      return new Ship(0, y,  true);
    }
    return new Ship(500, y, false);
  }


  // draws the list of ships
  public WorldScene drawWorld(WorldScene scene) {
    return this.rest.drawWorld(scene).placeImageXY(
        this.first.draw(), this.first.getXCoor(), this.first.getYCoor());
  }

  // moves all the ships in the list
  public ILoShips moveShips() {
    return new ConsLoShips(this.first.move(), this.rest.moveShips());
  }


  // filters out the ships that are not in the screen
  public ILoShips filter(IPred<Ship> pred) {
    if (pred.apply(this.first)) {
      return this.rest.filter(pred);
    } else {
      return new ConsLoShips(this.first, this.rest.filter(pred));
    }
  }

  // determines if the passed bullet collided with any of the Ships
  public boolean collided(Bullet b) {
    return this.computeDistance(
        this.first.getXCoor(), this.first.getYCoor(), b.getXCoor(), b.getYCoor())
        <= this.first.getRadius()
        || this.computeDistance(
            this.first.getXCoor(), this.first.getYCoor(), b.getXCoor(), b.getYCoor())
        <= b.getRadius()
        || this.rest.collided(b);
  }


  // computes the distance between two points
  double computeDistance(int x1, int y1, int x2, int y2) {
    return Math.sqrt( ((double) x2 - (double) x1) * ((double) x2 - (double) x1)
        + ((double) y2 - (double) y1) * ((double) y2 - (double) y1));
  }

  // returns the length of the list
  public int length() {
    return this.lengthHelper(0);
  }

  // helper for the length of a list
  public int lengthHelper(int start) {
    return this.rest.lengthHelper(start + 1);
  }

}

// represents an empty list of Ships
class MtLoShips implements ILoShips {

  Random rand = new Random();

  // constructor for an empty list of Ships
  MtLoShips() {
  }

  /*
   * methods:
   * createNewShips() ... ILoShips
   * createNewShipsHelper() ... ILoShips
   * createRandomShip() ... Ship
   * drawWorld() ... WorldScene
   * moveShips() ... ILoShips
   * filter() ... ILoShips
   * collided() ... boolean
   * computeDistance() ... double
   * length() ... int
   * lengthHelper() ... int
   * 
   * 
   */

  // creates a new list of ships
  public ILoShips createNewShips(int max, Random rand) {
    // int max = this.rand.nextInt(2) + 1;
    // return createNewShipsHelper(this, max);
    return createNewShipsHelper(this, max, rand);
  }

  //returns the passed list
  public ILoShips createNewShipsHelper(ILoShips originalList, int max, Random rand) {
    int limit = rand.nextInt(214) + 43;
    if (max == 0) {
      return new ConsLoShips(createRandomShip(limit, rand.nextInt(2)), originalList);
    }
    return new ConsLoShips(createRandomShip(limit, rand.nextInt(2)),
        this.createNewShipsHelper(originalList, max - 1, rand));
  }

  // create a random ship
  public Ship createRandomShip(int y, int randomSide) {

    if (randomSide % 2 == 0) {
      return new Ship(0, y,  true);
    }
    return new Ship(500, y, false);
  }


  // returns the empty scene
  public WorldScene drawWorld(WorldScene scene) {
    return scene;
  }

  // returns an empty list of ships
  public ILoShips moveShips() {
    return new MtLoShips();
  }

  // no list to filter, returns empty
  public ILoShips filter(IPred<Ship> pred) {
    return new MtLoShips();
  }


  // bullet cannot collide with an empty list of Ships
  public boolean collided(Bullet b) {
    return false;
  }


  // returns the length of an empty list
  public int length() {
    return 0;
  }

  // returns the accumulated length of a list
  public int lengthHelper(int start) {
    return start;
  }

}



//Represents a predicate to apply to a list of T
interface IPred<T> {
  // determines if the predicate T applies to whatever implements it
  boolean apply(T t);
}

//represents a class which checks if a bullet is on the screen
class BulletInScreen implements IPred<Bullet> {

  BulletInScreen() {}

  /*
   * methods:
   * apply() ... boolean
   */
  // checks if a bullets X and Y coordinates are within the screen range
  public boolean apply(Bullet t) {
    /* Template for Bullet:
     * fields:
     * t.x ... int
     * t.y ... int
     * t.radius ... int
     * t.multiplier ... int
     * t.xSpeed ... int
     * t.ySpeed ... int
     * 
     * methods:
     * t.move() ... IPiece
     * t.draw() ... WorldScene
     * t.getXCoor() ... int
     * t.getYCoor() ... int
     * t.getRadius() ... int
     * t.getMultiplier() ... int
     * 
     */

    return t.getXCoor() > 500
        || t.getYCoor() > 300
        || t.getXCoor() < 0
        || t.getYCoor() < 0; 
  }
}

//represents a class which checks if a ship is on the screen
class ShipInScreen implements IPred<Ship> {
  ShipInScreen() {
  }

  /*
   * apply() ... boolean
   */

  // checks if a bullets X and Y coordinates are within the range
  public boolean apply(Ship t) {
    /* Template for Ship
     * t.x ... int
     * t.y ... int
     * t.isMovingRight ... boolean
     * 
     * methods:
     * t.move() ... IPiece
     * t.draw() ... WorldScene
     * t.getXCoor() ... int
     * t.getYCoor() ... int
     */

    return t.getXCoor() > 500
        || t.getYCoor() > 300
        || t.getXCoor() < 0
        || t.getYCoor() < 0; 
  }
}

// class for the function 
// that adjusts the list of bullets with the collided bullets new value
class FilterCollidedBullets implements IPred<Bullet> {

  ILoShips ships;

  FilterCollidedBullets(ILoShips ships) {
    this.ships = ships;
  }

  /*
   * fields:
   * this.ships ... ILoShips
   * 
   * methods:
   * apply() ... boolean
   */
  // applies the collided method as the filter method
  public boolean apply(Bullet t) {

    /* Template for Bullet:
     * fields:
     * t.x ... int
     * t.y ... int
     * t.radius ... int
     * t.multiplier ... int
     * t.xSpeed ... int
     * t.ySpeed ... int
     * 
     * methods:
     * t.move() ... IPiece
     * t.draw() ... WorldScene
     * t.getXCoor() ... int
     * t.getYCoor() ... int
     * t.getRadius() ... int
     * t.getMultiplier() ... int
     * 
     */
    return this.ships.collided(t);
  }

}

// class for the function that removes any ship that collided with a bullet
class FilterCollidedShips implements IPred<Ship> {
  ILoBullets bullets;


  FilterCollidedShips(ILoBullets bullets) {
    this.bullets = bullets;
  }

  /*
   * fields:
   * this.bullets ... ILoBullets
   * 
   * methods:
   * apply() ... boolean
   */

  //applies the collided method as the filter method
  public boolean apply(Ship t) {

    /* Template for Ship
     * t.x ... int
     * t.y ... int
     * t.isMovingRight ... boolean
     * 
     * methods:
     * t.move() ... IPiece
     * t.draw() ... WorldScene
     * t.getXCoor() ... int
     * t.getYCoor() ... int
     */
    return this.bullets.collidedWith(t);
  }
}

class ExamplesILO{

}


