# PDS_Generator

Please ignore the test folder. It contains test material for bug fixing.

To generate file for the PDS Schema version 2.5 change directory into Generator 
and run Run.bash on the command line as follows: bash Run.bash

A number of files, expressing most data feilds, as well as their 
corropsonding add / change / delete files, will be generator and placed into
a GeeneratedFiles directory. 

The feilds that were left ungenerated are:
- ProcurementInstrumentIdentifier (multiple fields)
- RegulationURL                   (single field)
- ModificationDetails             (single field)

This solution only works for the PDS v2.5 Schema currently.

