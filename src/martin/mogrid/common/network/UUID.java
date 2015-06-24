/*
 * Created on 18/08/2006
 *
 * UUID.java
 * $Id: UUID.java,v 1.2 2000/10/18 14:52:49 bmahe Exp $
 * (c) COPYRIGHT MIT, INRIA and Keio, 2000.
 * 
 * Source: http://dev.w3.org/cvsweb/~checkout~/java/classes/org/w3c/util/UUID.java?rev=1.2
 */
package martin.mogrid.common.network;


/**
 * A UUID (from java.rmi.server.UID)
 * 
 * @version $Revision: 1.2 $
 * @author Benoît Mahé (bmahe@w3.org)
 */

public class UUID implements Payload {
   
   // * What does it look like:
   //   UUIDs represents an immutable Universally Unique IDentifier. 
   //   A UUID represents a 128-bit value (number), normally presented in a
   //   hexadecimal grouped-base form.
   //   The correct way to construct these identifiers is described in RFC 4122:
   //   <http://www.ietf.org/rfc/rfc4122.txt>
   //
   //   The formal definition of the UUID string representation is
   //   provided by the following ABNF:
   //
   //   UUID                   = time-low "-" time-mid "-"
   //                            time-high-and-version "-"
   //                            clock-seq-and-reserved
   //                            clock-seq-low "-" node
   //   time-low               = 4hexOctet
   //   time-mid               = 2hexOctet
   //   time-high-and-version  = 2hexOctet
   //   clock-seq-and-reserved = hexOctet
   //   clock-seq-low          = hexOctet
   //   node                   = 6hexOctet
   //   hexOctet               = hexDigit hexDigit
   //   hexDigit =
   //        "0" / "1" / "2" / "3" / "4" / "5" / "6" / "7" / "8" / "9" /
   //        "a" / "b" / "c" / "d" / "e" / "f" /
   //         "A" / "B" / "C" / "D" / "E" / "F"
   //
   //   The following is an example of the string representation of a UUID as a URN: 
   //   urn:uuid:f81d4fae-7dec-11d0-a765-00a0c91e6bf6
   //
   // * The nil UUID:
   //   A special type of UUID is guaranteed to not be unique, and easily recognised. 
   //   This is the nil UUID: 00000000-0000-0000-0000-000000000000. It can serve to 
   //   clear UUIDs and as a template while cunstructing new ones.

   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 7784735625328952033L;

   /** @serial Integer that helps create a unique UID. */
   private int unique;

   /** @serial Long used to record the time. The <code>time</code> will be used
    *          to create a unique UID.*/
   private long time;

   /** InetAddress to make the UID globally unique */
   private static String address;

   /** A random number */
   private static int hostUnique;

   /** Used for synchronization */
   private static Object mutex;
   private static long lastTime;
   private static long DELAY;

   
   
   private static String generateNoNetworkID() {
      String nid = Thread.activeCount() + System.getProperty("os.version")
                                        + System.getProperty("user.name")
                                        + System.getProperty("java.version");
      MD5 md5 = new MD5(nid);
      md5.processString();
      return md5.getStringDigest();
   }

   static {
      hostUnique = (new Object()).hashCode();
      mutex = new Object();
      lastTime = System.currentTimeMillis();
      DELAY = 10; // in milliseconds
      /*try {
         String s = InetAddress.getLocalHost().getHostAddress();
         MD5 md5 = new MD5(s);
         md5.processString();
         address = md5.getStringDigest();        
      } catch (UnknownHostException ex) {
         address = generateNoNetworkID();
      }*/
      
      String s = LocalHost.getLocalMacAddress();
      if ( s != null ) {
         MD5 md5 = new MD5(s);
         md5.processString();
         address = md5.getStringDigest(); 
      } else {
         address = generateNoNetworkID();         
      }
   }

   public UUID() {
      synchronized (mutex) {
         boolean done = false;
         while (!done) {
            time = System.currentTimeMillis();
            if (time < lastTime + DELAY) {
               // pause for a second to wait for time to change
               try {
                  Thread.sleep(DELAY);
               } catch (java.lang.InterruptedException e) {
               } // ignore exception
               continue;
            } else {
               lastTime = time;
               done = true;
            }
         }
         unique = hostUnique;
      }
   }
   
   public String toString() {
      return Integer.toString(unique, 16) + "-" + Long.toString(time, 16) + "-" + address;
   }

   public boolean equals(Object obj) {
      if ((obj != null) && (obj instanceof UUID)) {
         UUID uuid = (UUID) obj;
         boolean result = (unique == uuid.unique && time == uuid.time && address.equals(UUID.address));
         return result;
      } else {
         return false;
      }
   }

   
   //TEST
   public static void main(String args[]) {
      UUID uuid = new UUID();
      System.out.println( uuid + "\n>> " +
                          uuid.unique + "-" + uuid.time+ "-" + UUID.address + "\n>> " +
                          uuid.time+"\n");
      uuid = new UUID();
      System.out.println( uuid + "  " +uuid.time+"\n");
      uuid = new UUID();
      System.out.println( uuid + "  " +uuid.time+"\n");
      uuid = new UUID();
      System.out.println( uuid + "  " +uuid.time+"\n");
   }

}
