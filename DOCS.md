
### Adding JMXL to your project
By using darktree.net maven:
```gradle
// add to the 'repositories' block
maven {
    allowInsecureProtocol = true // i'm working on this
    url 'http://maven.darktree.net'
}

// add to the 'dependencies' block, change '1.0' to the desired version
modImplementation "net.darktree:JMXL:1.0"
include "net.darktree:JMXL:1.0"
```
Or by dropping the JMXL jar into `./run/mods` directory

### Using JMXL
To mark a model as using JMXL features add `"jmxl": true` to the root tag,
then in every model element you can add a `"jmxl_layer": ...` tag to set the render layer of that element

The permitted values are:  

| Layer           | Description                                                                    |
|-----------------|--------------------------------------------------------------------------------|
| "DEFAULT"       | Use blending behavior of BlockRenderLayer associated with the block            |
| "SOLID"         | No blending. Used for most normal blocks (no transparency)                     |
| "CUTOUT"        | Pixels with alpha >0.5 are rendered as if SOLID, other pixels are not rendered |
| "TRANSLUCENT"   | Pixels are blended with the background according to alpha color values         |
| "CUTOUT_MIPPED" | Same as CUTOUT, but mipmaps are enabled                                        |

Example model:
```json
{
    "textures": {
        "2": "led:block/base",
        "particle": "led:block/particle"
    },
    "jmxl": true,
    "elements": [
        {
            "from": [4, 0, 4],
            "to": [12, 1, 12],
            "jmxl_layer": "TRANSLUCENT",
            "faces": {
                "north": {"uv": [0, 15, 8, 16], "texture": "#2"},
                "east": {"uv": [0, 12, 8, 13], "texture": "#2"},
                "south": {"uv": [0, 14, 8, 15], "texture": "#2"},
                "west": {"uv": [0, 13, 8, 14], "texture": "#2"},
                "up": {"uv": [8, 8, 16, 16], "texture": "#2"},
                "down": {"uv": [8, 8, 16, 16], "texture": "#2"}
            }
        }
    ]
}
```