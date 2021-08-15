### Version 0.7.4

- Add a note related to memory issues
- Add message when the file reading is complete
- Disable Ignore List temporarily since it could cause a missing dependency of flow feature compononts
- Fix non-terminated flows do not receive flow finalization after all packets are parsed and analysed
- Fix the worker do not acquire the lock of the dispatcher while calling wait and notify

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