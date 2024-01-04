# Bit Flags

This is a new protocol proposal in which the <code>dimension</code> variable inside <code>Packet1Login</code> is broken up into individual bits, rather than the entire byte being used.

This will effectively expand it from one variable to 8, with one big caveat: Each of the 8 'variables' must be a 0, or a 1. Reduced to a boolean. However, luckily for us that's all we ever use it for anyway. We will still be able to indicate critical information about IP forwarding and such.

## Why is this useful?

It would allow more information to be passed through to plugins while still maintaining it's function for determining IP forwarding status. With the current system, if a server wanted to have a custom client with custom packets to interact with a custom plugin, they would not be able to do so while the <code>dimension</code> variable is in use by BungeeCord or Release2Beta. This may not be an issue for most servers, but OSM and GMC both use BungeeCord and it would be useful to us.