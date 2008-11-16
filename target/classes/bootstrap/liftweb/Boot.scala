package bootstrap.liftweb

import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import Helpers._
import net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}
import java.sql.{Connection, DriverManager}
import com.yuroyoro.liftropy.model._
 
/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    // where to search snippet
    LiftRules.addToPackages("com.yuroyoro.liftropy")     

    Schemifier.schemify(true, Log.infoF _, TropyEntry)

    // Build SiteMap
 val entries = Menu(Loc("Random", "/", "Random")) :: 
      Menu(Loc("create","/create","create")) ::
      Menu(Loc("edit", "/edit", "edit")) :: 
      Menu(Loc("show", "/show", "show")) :: Nil

    LiftRules.setSiteMap(SiteMap(entries:_*))
    
  }
  
  LiftRules.addRewriteBefore {  
   case RewriteRequest(ParsePath( "index" :: Nil , _,_), _, _) =>  
        RewriteResponse("show" ::Nil) 
   case RewriteRequest(ParsePath( "show"::tropyId :: _ , _ ,_ ), _, _) =>  
        RewriteResponse("show"::Nil , Map("tropyId" -> tropyId)) 
   case RewriteRequest(ParsePath( "edit"::tropyId :: _ , _ ,_ ), _, _) =>  
        RewriteResponse("edit"::Nil , Map("tropyId" -> tropyId))  
  }
  
}


object DBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Can[Connection] = {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
      val dm = DriverManager.getConnection("jdbc:derby:lift_example;create=true")
      Full(dm)
    } catch {
      case e : Exception => e.printStackTrace; Empty
    }
  }
  def releaseConnection(conn: Connection) {conn.close}
}

