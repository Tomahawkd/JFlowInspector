### Version 0.7.3

- Add a temporary option that ignores IP and TCP options field for faster parsing
- Add thread waiting and notifying mechanism to block workers that has no packet to process
- Add a writer to avoid creating file output stream frequently
- Fix dispatcher workload algorithm which would block accepting new packets
- Fix dispatcher which may not update current packet timestamp in all workers.
- Fix HTTP feature class construction causing redundant scanning class to construct HTTP feature processors

### Version 0.7.2

- Add support for TCP Option parse
- Fix a bug that could split the last ACK from the original FIN terminated flow
- Adjust code file structure

### Version 0.7.1

- Migrate project from [CICFlowMeter Mk.6](https://github.com/Tomahawkd/CICFlowMeter-Mk.6)