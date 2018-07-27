Development Environment: Eclipse IDE

Known issues:

-  Under certain conditions, the polygon renderer
   will flip face normals (by reversing vertex order)
   after backface culling. This is later corrected by
   the shader.

-  Near and far clipping is done separately in camera
   space. Maybe it should be combined with frustrum
   clipping in NDC.

How to run:

1. Import folder into Eclipse and run or,
2. Run the A4.jar file using the following command:

   java -jar A4.jar
