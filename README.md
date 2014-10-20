# Thumb 2

Peter Swire - swirepe@swirepe.com

## What is it?

I rewrote my project [thumb](https://github.com/swirepe/thumb) to be asyncronous.  It's about seven times faster now.  I learned a lot in a year, I guess.

It also can behave as a server.

## What does it look like?

![Before](cat.png)

![After](thumb-cat.png)

## Usage

### As a Library

All you need is an instance of `ThumbnailerService`.  You pass it in `BufferedImage` objects and get back listenable futures.  Alternatively, you can use the `Thumbnailer` class directly.  It's a callable, so you can submit it to a theadpool and get your own futures.

To use the server in the library, use the `ThumbnailServer` class in the `net` subpackage.  It's default port is **9200**, but that's a parameter.

### As a Command-line Tool

Help

    ~/pers/thumb2  $ java -jar jar/thumb.jar --help
    Thumbnail generator - swirepe@swirepe.com
    Usage:
        --threads int	Number of threads to allocate to the thumbnailer service.
        --width int	Width of the thumbnails to return.
        --height int	Height of the thumbnails to return.
        --no-resize	Do not resize the image before finding a thumbnail.
        --prefix	Prefix to attach to generated thumbnails.
        --quiet	Only report the output names, nothing else.
        --silent	Disable logging.

Running

    ~/pers/thumb2  $ java -jar jar/thumb.jar cat.png
    /home/swirepe/pers/thumb2/cat.png	Reading.
    /home/swirepe/pers/thumb2/cat.png	Submitting for processing.
    Oct 19, 2014 7:35:12 PM com.swirepe.thumb.Thumbnailer makeThumbnail
    INFO: Thumbnailing image image: 1600x1200 -> 100x100
    Oct 19, 2014 7:35:12 PM com.swirepe.thumb.Thumbnailer resize
    INFO: Resizing base image by scale: 0.1875x0.25
    Oct 19, 2014 7:35:12 PM com.swirepe.thumb.Thumbnailer resize
    INFO: Resized base image: 300x300
    Oct 19, 2014 7:35:12 PM com.swirepe.thumb.processing.InterestingRegionFinder getWorkers
    INFO: Created 40400 InterestingRegionWorker objects.
    Oct 19, 2014 7:35:13 PM com.swirepe.thumb.processing.InterestingRegionFinder resolveFutures
    INFO: Most interesting region found: entropy = -242.01354518854316, x = 122, y = 36
    /home/swirepe/pers/thumb2/cat.png	Writing thumbnail to thumb-cat.png
    /home/swirepe/pers/thumb2/cat.png	Finished.
    Thumb Service terminated in 1456ms
    All files processed in 1767ms

### As a Server

Help

    ~/pers/thumb2  $ java -jar jar/thumb-server.jar --help
    Thumbnail server - swirepe@swirepe.com
    Usage:
        --help	This message.
        --port int	Port for the server to listen on.
        --threads int	Number of threads to allocate to the thumbnailer service.
        --width int	Width of the thumbnails to return.
        --height int	Height of the thumbnails to return.
        --no-resize	Do not resize the image before finding a thumbnail.
        --silent	Disable logging.

Running

    ~/pers/thumb2  $ java -jar jar/thumb-server.jar       
    Oct 19, 2014 7:40:15 PM com.swirepe.thumb.net.ThumbnailServer run
    INFO: Server started with port = 9200, width = 100, height = 100
    Oct 19, 2014 7:40:15 PM com.swirepe.thumb.net.ThumbnailServer run
    INFO: Listening on port 9200
        
Then

    nc localhost 9200 < cat.png > thumb-cat.png

## License and Credits

This project is released under the Apache v2 license.  You can read about that in LICENSE.md.  If you use it though, drop me a line?  I'd like to know how it works for you.

This project uses the excellent [Google Guava](https://code.google.com/p/guava-libraries/) library, which is also under the Apache v2 license.


