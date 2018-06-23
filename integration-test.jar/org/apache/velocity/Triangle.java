/**
 * 
 */
package org.apache.velocity;

/**
 * @author mark
 *
 */
public class Triangle implements Shape {

  /* (non-Javadoc)
   * @see org.apache.velocity.Shape#getName()
   */
  @Override
  public Integer getNumberOfSides() {
    return 3;
  }

}
