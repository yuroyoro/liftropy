package com.yuroyoro.liftropy.snippet

import scala.xml.{NodeSeq,Text,Group}
import scala.Math._

import net.liftweb.http._
import net.liftweb.http.S._
import net.liftweb.util._
import com.yuroyoro.liftropy.model.TropyEntry

class TropySnippet {
  private object selectedEntry extends RequestVar[Can[TropyEntry]](Empty)

  def create(xhtml:Group) :NodeSeq = {
    <center>
        <table>
            {selectedEntry.is.openOr( new TropyEntry).toForm( Empty , saveEntry _ )}
        </table>
        <ul>
            <li><a href="/">Cancel</a></li>
            <li><input type="submit" value="Create"/></li>
       </ul>
    </center>
  }
  
  def saveEntry( entry:TropyEntry) = entry.validate match {
    case Nil => entry.save ; redirectTo("/show/" + entry.tropyId)
    case x => error(x);selectedEntry(Full(entry))
  }
  
  def show(xhtml:Group) :NodeSeq = S.param("tropyId") match {
    case Empty => getRandomTropyId match{
      case 0 => redirectTo("/create")
      case tropyId => redirectTo("/show/" + tropyId) 
      }
    case tropyId => TropyEntry.findByKey( tropyId.open_!.toLong) match{
      case Empty => redirectTo("/")
      case entry => selectedEntry(entry); <p>{entry.open_!.entry}</p>
    }
  }
    
  def getRandomTropyId : Long  ={
    val rand = new Random()
    TropyEntry.count match {
      case 0 => 0
      case x => (abs(rand.nextLong) % x) + 1
    }
  }
  
  def editLink(xhtml:Group) :NodeSeq = <a href={"/edit/" + selectedEntry.open_!.tropyId}>Edit</a>

  def edit(xhtml: Group): NodeSeq = S.param("tropyId") match {
    case Empty => getRandomTropyId match{
      case 0 => redirectTo("/create")
      case tropyId => redirectTo("/show/" + tropyId) 
      }
    case tropyId => TropyEntry.findByKey( tropyId.open_!.toLong) match{
      case Empty => redirectTo("/create")
      case entry => {
        selectedEntry(entry); 
        <center>
            <table>
                {entry.open_!.toForm(Empty, saveEntry _) }
            </table>
            <ul>
               <li><a href={"/show/" + selectedEntry.open_!.tropyId}>Cancel</a></li>
               <li><input type="submit" value="Edit"/></li>
             </ul>
        </center>
      }
    }
  }
    

}

