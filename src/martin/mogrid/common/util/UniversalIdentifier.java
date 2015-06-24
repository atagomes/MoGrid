/*
 * Created on 26/09/2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package martin.mogrid.common.util;

import martin.mogrid.common.network.Payload;
import martin.mogrid.common.network.UUID;

public class UniversalIdentifier implements Payload {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 4955844629680621146L;
   
   //  Comments:
   //
   // * What does it look like:
   //   UUIDs represents an immutable Universally Unique IDentifier. 
   //   A UUID represents a 128-bit value (number), normally presented in a
   //   hexadecimal grouped-base form.
   //   The correct way to construct these identifiers is described in RFC 4122:
   //   <http://www.ietf.org/rfc/rfc4122.txt>
   //   <http://www.itu.int/ITU-T/asn1/uuid.html>
   //
   //   The following is an example of the string representation of a UUID: 
   //   >> f81d4fae-7dec-11d0-a765-00a0c91e6bf6
   //
   // * The nil UUID:
   //   A special type of UUID is guaranteed to not be unique, and easily recognised. 
   //   This is the nil UUID: 00000000-0000-0000-0000-000000000000. It can serve to 
   //   clear UUIDs and as a template while cunstructing new ones.

   public static final String NULL_UUID = "00000000-0000-0000-0000-000000000000";
   
   private static final String ID_SEPARATOR = "@"; 
   private              UUID   uuid         = null;  
   private              String subject      = null;
   private              String identifier   = ID_SEPARATOR + NULL_UUID;

   public UniversalIdentifier(String subject) {
      uuid = new UUID();
      this.subject = subject;
      
      identifier = subject + ID_SEPARATOR + uuid;
   }

   public String getIdentifier() {
      return identifier;
   }

   public String getSubject() {      
      return subject;
   }
   
   public String toString() {
      return identifier;
   }
   
   
   // Necessario reimplementar os metodos equals() e hashCode() pois subclasses de
   // Identifier sao usadas como chaves em Hashtables
   public boolean equals(Object obj) {
      // Step 1: this.equals(null) should return false
      if ( obj == null ) {
         return false;
      }
      // Step 2: Perform an == test (reflexive)
      if ( this == obj ) { 
          return true;
      }
      // Step 3: Instance of check
      if( obj instanceof UniversalIdentifier ) { 
         // Step 4: For each important field, check to see if they are equal
         // For primitives use ==
         // For objects use equals() but be sure to also handle the null case first
         UniversalIdentifier id = (UniversalIdentifier) obj;
         boolean idTest = ( subject.equals(id.subject) && uuid.equals(id.uuid) );
         return (idTest);
      } 
      return false;
   }

   public int hashCode() {
      // Always return the same value for each object because you always return the 
      // same value for all objects. You also return identical hashCode values when
      // 2 objects test as equals because you always return identical hashCode values. 
      // There is no requirement to return different hashCode values when
      // two objects test as not equal.
      
      //Turn the object's fields into a string, concatenate the strings,
      //and return the resulting hashcode multiply with prime
      return identifier.hashCode()*3;
   }
   
}
