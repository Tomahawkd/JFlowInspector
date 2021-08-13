# JFlowInspector
JFlowInspector is a tool to inspect network traffic, identify network flows and extract preset
features to CSV file.

## History
Inspired by [CICFlowMeter](https://github.com/ahlashkari/CICFlowMeter), the project originally
is forked to [CICFlowMeter Mk.6](https://github.com/Tomahawkd/CICFlowMeter-Mk.6). After a massive
code refactor, CICFlowMeter Mk.6 is able to extract more features, not only the TCP features
but also HTTP features.

To read HTTP data from TCP stream, CICFlowMeter Mk.6 is refactored again to add the ability to 
reassemble the HTTP data from several TCP segments. However, the TCP reorder and reassembly slows
down the whole inspecting procedure significantly.

The third code refactor comes with multi-threading and a faster pcap file parser. This is the 
current CICFlowMeter Mk.6. For now, it is way different from the original CICFlowMeter, and 
I'm tend to provide more features for CICFlowMeter Mk.6. Therefore, the original CICFlowMeter Mk.6
project is migrate to a new repository, which here is the place for the new generation of the 
CICFlowMeter and CICFlowMeter Mk.6.

## Prerequisite
1. Java 8
2. jnetpcap native library and use `-Djava.library.path` to link native library with jar (you can find
   it in `./lib/jnetpcap` or download it from [original project](https://sourceforge.net/projects/jnetpcap/)).

Note:
Highly recommended allocating JFlowInspector with larger memory using `-Xmx` if you are about to
processing large number of network flows.
My configuration is allocating 4G of the memory (`-Xmx4G`)

## Build
Clone the code and its submodule and use maven to create jar.

Note: 
1. The repo is only tested on Windows platform.
2. The native library is acquired from the original CICFlowMeter repo.
3. For more information about jnetpcap, please follow the [link](https://sourceforge.net/projects/jnetpcap/).
4. The tool will generate tons of logs while running, use `--quiet` to stop this.

## Developing Note
Since this project is still at early development, and it is still under my 
graduate project, internal structure, behaviour may vary and limit 
in different version.

## Commandline Help
```
Usage: <main class> [options] Pcap file or directory.
  Options:
    -a, --act_time
      Setting timeout interval for an activity.
      Default: 5000000
    -c, --continue
      Indicate the files in input dir are continuous.
      Default: false
    --debug
      Show debug output (sets logLevel to DEBUG)
      Default: false
    -q, --flow_queue
      Set the queue length waiting for flow process
      Default: 256
    -t, --flow_thread
      Set the thread count to process flows
      Default: 5
    -f, --flow_time
      Setting timeout interval for a flow.
      Default: 120000000
    -h, --help
      Prints usage for all the existing commands.
    -m, --mode
      Mode selection.
      Default: DEFAULT
      Possible Values: [DEFAULT, SAMPLING, FULL, ONLINE]
    -n, --no
      Ignores specific feature (use as -no <feature1>,<feature2>)
      Default: []
    --noassemble
      Disable TCP Reassembing
      Default: false
    --old
      Use Jnetpcap Parser which is stable but slow.
      Default: false
    -1, --one_file
      Output only one file.
      Default: false
    --quiet
      No output (sets logLevel to NONE)
      Default: false
  * -o, -output
      Output directory.
```
