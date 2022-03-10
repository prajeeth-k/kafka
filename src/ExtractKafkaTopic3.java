import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.Node;

import java.util.Collection;
import java.lang.String;
import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import ora.metadata.table.EmployeeDeserializer;
//import ora.metadata.table.EmployeeMetadata;
import mylibs.SaveOffsetToTopic;
import ora.util.DB;

@SuppressWarnings("unchecked")
class KeyDeserializer12 implements Deserializer<String> {
   Map<String, String> deserial;
   KeyDeserializer12 () {
      deserial = new HashMap<String, String>();
      deserial.put ("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
   }
   //void configure (Map<String,String> deserial, boolean isKeytrue) {}
   @Override public void configure(Map<String, ?> arg0, boolean arg1) {}
   @Override
   public String deserialize (String topic, byte[] data) {
        String x = new String (data);
        return x;
   }
   @Override public void close () {}
}

@SuppressWarnings("unchecked")
class ValueDeserializer12 implements Deserializer<String> {
   Map<String, String> deserial;
   ValueDeserializer12 () {
      deserial = new HashMap<String, String>();
      deserial.put ("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
   }
   // void configure (Map<String,String> deserial, boolean isKeytrue) {}
   @Override public void configure(Map<String, ?> arg0, boolean arg1) {}
   @Override
   public String deserialize (String topic, byte[] data) {
        String x = new String (data);
        return x;
   }
   @Override public void close () {}
}

@SuppressWarnings("unchecked")
public class ExtractKafkaTopic3 {
   Properties props;
   KeyDeserializer12 k;
   ValueDeserializer12 v;
   int recordCount = 1;
   EmployeeDeserializer m;
   //EmployeeMetadata m;
   DB dbObject = new DB ();

   SaveOffsetToTopic offsetSave;
   public ExtractKafkaTopic3 () {
      m = new EmployeeDeserializer ();
      //m = new EmployeeMetadata ();
      //offsetSave = new SaveOffsetToTopic ("consumer_offset", 0);
      dbObject.setUser ("prajeeth2");
      dbObject.setPassword ("prajeeth2");
      dbObject.getConnection ();
   }
   public void printRows () {
        k = new KeyDeserializer12 ();
        v = new ValueDeserializer12 ();
        //public void subscribe (Arrays.asList ("topic1"), ConsumerRebalanceListener listener) {}
   }
   public void kafkaConsumer () {
      final AtomicBoolean closed = new AtomicBoolean(false);
      /*PartitionInfo partionInfo = new PartitionInfo ();
      Node[] nodesConnection = partitionInfo.replicas ();
      int nodesArray;
      for (nodesArray = 0; nodesArray < nodesConnection.length; nodesArray++) {
          System.out.println ("Connected to : " + nodesConnection[nodesArray].host () + ", " + nodesConnection[nodesArray].id ());
      }
      
      System.out.println ("Partition Info : " + partitionInfo.partition ());
      System.out.println ("Topic Info : " + partitionInfo.topic ());
      */
      props = new Properties();
      props.setProperty ("bootstrap.servers", "localhost:9092");
      props.setProperty("group.id", "test");
      props.setProperty("enable.auto.commit", "false");
      props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      String pVal = props.getProperty ("key.deserializer");
      KafkaConsumer<String, String> consumer = new KafkaConsumer<> (props);      
      // Setting to offset 50
      
      //consumer.subscribe (Arrays.asList ("topic1"));
      consumer.unsubscribe ();
      TopicPartition assignedPartition = new TopicPartition ("topic1", 0);
      consumer.assign (Arrays.asList (assignedPartition));
      final int minBatchSize = 200;
      List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
      System.out.println ("Oracle Database, Apache Kafka, ECCOS SQL Server Database Replication");
      
      Set<TopicPartition> topicPartitionSet = consumer.assignment ();
      System.out.println ("Partition Set Size : " + topicPartitionSet.size ());
      Iterator<TopicPartition> partitionIterator = topicPartitionSet.iterator ();
      while (partitionIterator.hasNext ()) {
          System.out.println ("Partition from partition iterator : " + partitionIterator.next ().partition ());
      }
      consumer.seek (new TopicPartition ("topic1", 0), 50); 
      while (!closed.get()) {
         ConsumerRecords<String, String> records = consumer.poll (Duration.ofMillis (2000));
         for (TopicPartition partition : records.partitions ()) {
            System.out.println ("Partition : " + partition.partition ());
            System.out.println ("Topic : " + partition.topic ());
         }
         for (ConsumerRecord<String, String> record : records) {
            //System.out.println ("Consumer Records count : " + records.count ());
            //buffer.add(record);
            System.out.println ("# of records in transacton : " + recordCount);
            ++recordCount;
            // System.out.println ("Topic : " + record.topic () + " | Key : " + record.key () + " | Value : " + record.value ());
            try {
               m = new EmployeeDeserializer ();
               //m = new EmployeeMetadata ();
               m.setInput (record.value ());
            } catch (Exception e) {
               e.printStackTrace ();
            }
            dbObject.load (m);
            //System.out.println ("Last consumed offset : " + record.offset ());
            //offsetSave.saveOffset (record.offset ());            
            /* Need to save the last consumed offset to a topic
            ** so that the next time the consumer starts after a crash,
            ** it can start from the right offset
            */
            //m.loadData ();
         }
        if (buffer.size () >= minBatchSize) {
            consumer.commitSync();
            buffer.clear();
         }
      }
   }
   public static void main (String[] a) {
      ExtractKafkaTopic3 extractKafkaTopic = new ExtractKafkaTopic3 ();
      extractKafkaTopic.kafkaConsumer (); 
   }
}


