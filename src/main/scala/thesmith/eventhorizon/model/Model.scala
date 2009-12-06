package thesmith.eventhorizon.model

import org.scala_libs.jpa.LocalEMF
import net.liftweb.jpa.RequestVarEM

object Model extends LocalEMF("transactions-optional") with RequestVarEM

