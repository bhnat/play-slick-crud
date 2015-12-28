package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.User

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for users.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import driver.api._

  /**
   * Here we define the table. It will have a name of users
   */
  private class UserTable(tag: Tag) extends Table[User](tag, "user") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The first name column */
    def firstName = column[String]("first_name")

    /** The last name column */
    def lastName = column[String]("last_name")

    /** The auth token column */
    def authToken = column[String]("auth_token")

    /** The email column */
    def email = column[String]("email")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the User object.
     *
     * In this case, we are simply passing the id, name and page parameters to the User case classes
     * apply and unapply methods.
     */
    def * = (id, firstName, lastName, authToken, email) <> ((User.apply _).tupled, User.unapply)
  }

  /**
   * The starting point for all queries on the user table.
   */
  private val users = TableQuery[UserTable]

  /**
   * Create a user with the given name and age.
   *
   * This is an asynchronous operation, it will return a future of the created user, which can be used to obtain the
   * id for that user.
   */
  def create(firstName: String, lastName: String, authToken: String, email: String): Future[User] = db.run {
    // We create a projection of just the data columns, since we're not inserting a value for the id column
    (users.map(u => (u.firstName, u.lastName, u.authToken, u.email))
      // Now define it to return the id, because we want to know what id was generated for the user
      returning users.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((fields, id) => User(id, fields._1, fields._2, fields._3, fields._4))
    // And finally, insert the user into the database
    ) += (firstName, lastName, authToken, email)
  }

  /**
   * List all the users in the database.
   */
  def list(id:Long): Future[Seq[User]] = db.run {
    users.filter(_.id === id).result
  }

  def list(): Future[Seq[User]] = db.run {
    users.result
  }

  def delete(id:Long) = db.run {
    users.filter(_.id === id).delete
  }

  def update(id: Long, user: User) = db.run {
    users.filter(_.id === id).update(user)
  }
}
