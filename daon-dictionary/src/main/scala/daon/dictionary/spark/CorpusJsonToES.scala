package daon.dictionary.spark

import org.apache.spark.sql._
import org.elasticsearch.spark.sql._

import scala.collection.mutable.ArrayBuffer

object CorpusJsonToES {


//  {"seq":1,"word":"!","tag":"sf","irrRule":null,"prob":7.3839235,"subWords":null,"desc":""}
  case class Keyword(word: String, tag: String, tf: Long, prop: Double)


  def main(args: Array[String]) {


    val spark = SparkSession
      .builder()
      .appName("daon dictionary")
      .master("local[*]")
      .config("es.nodes", "localhost")
      .config("es.port", "9200")
      .config("es.index.auto.create", "true")
      .getOrCreate()

    readJsonWriteEs(spark)

  }


  case class Sentence(sentence: String, eojeols: ArrayBuffer[Eojeol] = ArrayBuffer[Eojeol](), word_seqs: ArrayBuffer[Long] = ArrayBuffer[Long]())

  case class Eojeol(seq: Long, surface: String, offset: Long, morphemes: ArrayBuffer[Morpheme] = ArrayBuffer[Morpheme]())

  case class Morpheme(seq: Long, word: String, tag: String,
                      p_outer_seq: Option[Long] = None, p_outer_word: Option[String] = None, p_outer_tag: Option[String] = None,
                      n_outer_seq: Option[Long] = None, n_outer_word: Option[String] = None, n_outer_tag: Option[String] = None,
                      p_inner_seq: Option[Long] = None, p_inner_word: Option[String] = None, p_inner_tag: Option[String] = None,
                      n_inner_seq: Option[Long] = None, n_inner_word: Option[String] = None, n_inner_tag: Option[String] = None
                      )


  private def readJsonWriteEs(spark: SparkSession) = {

    
    import spark.implicits._

    val df = spark.read.json("/Users/mac/work/corpus/sejong_utagger.json")
    df.createOrReplaceTempView("raw_sentence")

    val new_df = df.map(row =>{
      val sentence = row.getAs[String]("sentence")
      val eojeols = row.getAs[Seq[Row]]("eojeols")
      val s = Sentence(sentence)

      eojeols.indices.foreach(e=>{
        val eojeol = eojeols(e)

        val eojeolSeq = eojeol.getAs[Long]("seq")
        val surface = eojeol.getAs[String]("surface")
        val offset = eojeol.getAs[Long]("offset")

        val ne = Eojeol(seq = eojeolSeq, surface = surface, offset = offset)

        val morphemes = eojeol.getAs[Seq[Row]]("morphemes")

        morphemes.indices.foreach(m=>{
          val morpheme = morphemes(m)

          val seq = morpheme.getAs[Long]("seq")
          val word = morpheme.getAs[String]("word")
          val tag = morpheme.getAs[String]("tag")

          s.word_seqs += seq

          val prev_outer = morpheme.getAs[Row]("prevOuter")
          val next_outer = morpheme.getAs[Row]("nextOuter")
          val prev_inner = morpheme.getAs[Row]("prevInner")
          val next_inner = morpheme.getAs[Row]("nextInner")

          var p_outer_seq = None : Option[Long]
          var p_outer_word = None : Option[String]
          var p_outer_tag = None : Option[String]
          if(prev_outer != null) {
            p_outer_seq = Option(prev_outer.getAs[Long]("seq"))
            p_outer_word = Option(prev_outer.getAs[String]("word"))
            p_outer_tag = Option(prev_outer.getAs[String]("tag"))
          }

          var n_outer_seq = None : Option[Long]
          var n_outer_word = None : Option[String]
          var n_outer_tag = None : Option[String]
          if(next_outer != null) {
            n_outer_seq = Option(next_outer.getAs[Long]("seq"))
            n_outer_word = Option(next_outer.getAs[String]("word"))
            n_outer_tag = Option(next_outer.getAs[String]("tag"))
          }
          
          var p_inner_seq = None : Option[Long]
          var p_inner_word = None : Option[String]
          var p_inner_tag = None : Option[String]
          if(prev_inner != null) {
            p_inner_seq = Option(prev_inner.getAs[Long]("seq"))
            p_inner_word = Option(prev_inner.getAs[String]("word"))
            p_inner_tag = Option(prev_inner.getAs[String]("tag"))
          }

          var n_inner_seq = None : Option[Long]
          var n_inner_word = None : Option[String]
          var n_inner_tag = None : Option[String]
          if(next_inner != null) {
            n_inner_seq = Option(next_inner.getAs[Long]("seq"))
            n_inner_word = Option(next_inner.getAs[String]("word"))
            n_inner_tag = Option(next_inner.getAs[String]("tag"))
          }


          val nm = Morpheme(seq, word, tag, 
                            p_outer_seq, p_outer_word, p_outer_tag,
                            n_outer_seq, n_outer_word, n_outer_tag,
                            p_inner_seq, p_inner_word, p_inner_tag,
                            n_inner_seq, n_inner_word, n_inner_tag
                          )

//          println(p_outer_seq, next_outer, prev_inner, next_inner)

          ne.morphemes += nm
        })

        s.eojeols += ne

      })

      s
    })

    new_df.printSchema()
    new_df.show(10)

    new_df.saveToEs("corpus/sentences")

  }
}
