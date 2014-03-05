extruder
========

Extrude borders of the individual tiles in a tilemap source file

usage
=====

java -jar extruder.jar [tile_width] [tile_height] src_file dst_file (optional)

Executing the above command will load the specified file (src_file), extrude the borders of each tile in the file and write the resulting image to the destination file.

Note that the width and height of the source image must be evenly dividable by the specified tile_width and tile_height

If dst_file is ommitted the src_file will be overwritten.