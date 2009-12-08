package thesmith.eventhorizon.model

import org.junit._
import Assert._

@Test
class AccountTest {
  val account: Account = new Account()

  @Before
  def setUp() = {
    SetupEnv.setUp()
    account.domain = "domain"
    account.personId = "thesmith"
    account.username = "thesmith"
    Model.persistAndFlush(account)
    
    val acc2 = new Account()
    acc2.domain = "something"
    acc2.personId = "blah"
    acc2.username = "fjdklsfjdkls"
    Model.persistAndFlush(acc2)
    System.err.println("----------"+acc2.id)
  }

  @After
  def tearDown(): Unit = {
    Model.createQuery("delete from thesmith.eventhorizon.model.Account").executeUpdate()
    Model.close()
    SetupEnv.tearDown()
  }

  @Test
  def testShouldPersistAccount() = {
    System.err.println("------------"+account.id)
    assertNotNull(account.id)
  }

  @Test
  def testShouldFindAccount() = {
    assertNotNull(account.id)
    val acc = Model.find(classOf[Account], account.id)
    assertNotNull(acc)
    assertFalse(acc.isEmpty)
    assertEquals(account.username, acc.get.username)
  }

  @Test
  def testShouldFindAccounts() = {
    val a = new Account()
    a.personId = "jlkldfs"
    a.domain = "fjkdls"
    a.username = "jfkdls"
    Model.persistAndFlush(a)

    val accs = Model.createQuery[Account]("select e from thesmith.eventhorizon.model.Account e").getResultList
      
    assertNotNull(accs)
    assertFalse(accs.isEmpty)
    assertEquals(2, accs.size)

    accs.foreach((acc: Account) => assertNotNull(acc.username))
  }
}
