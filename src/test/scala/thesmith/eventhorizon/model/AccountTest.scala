package thesmith.eventhorizon.model

import org.junit._
import Assert._

import thesmith.eventhorizon._

class AccountTest extends LocalDatastoreTestCase {
  val account: Account = new Account()

  override def setUp() = {
    super.setUp
    account.domain = "domain"
    account.personId = "thesmith"
    account.username = "thesmith"
    Model.persistAndFlush(account)
    assertNotNull(account.id)
  }

  override def tearDown(): Unit = {
    super.tearDown
    Model.createQuery("delete from Account ac").executeUpdate()
  }

  def testShouldPersistAccount() = {
    assertNotNull(account.id)
  }

  def testShouldFindAccount() = {
    val acc = Model.find(classOf[Account], account.id)
    assertNotNull(acc)
    assertFalse(acc.isEmpty)
    assertEquals(account.username, acc.get.username)
  }

  def testShouldFindAccounts() = {
    val a = new Account()
    a.personId = "jlkldfs"
    a.domain = "fjkdls"
    a.username = "jfkdls"
    Model.persistAndFlush(a)

    val accs = Model.createQuery[Account]("select e from Account e").getResultList
      
    assertNotNull(accs)
    assertFalse(accs.isEmpty)
    assertEquals(2, accs.size)

    accs.foreach((acc: Account) => assertNotNull(acc.username))
  }
}
