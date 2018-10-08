#!/usr/bin/env python3

import os
import sys
import zipfile
import shutil
import subprocess
import json
import glob




def do_zip_assemble(config):
    print('Starting artifact build with config:')
    print(json.dumps(config, indent=4))
    
    src_files = config['src'] # type: dict
    test_files = config['test']

    print('Copying to temp directory...')
    shutil.copytree('.', './__temp')
    os.chdir('./__temp')

    if src_files.get('clean', True):
        print('Deleting spurious source files before test...')
        for f in os.listdir(src_files['src_path']):
            if f not in src_files['include']:
                print('    Deleted', f)
                os.unlink(src_files['src_path'] + '/' + f)
            else:
                print('    Kept', f)

        print('Executing tests...')
        subprocess.check_call(['mvn', 'clean', 'test', '-B'], shell=True)
    else:
        print('Skipped clean testing...')

    print('Compiling artifact zip...')

    if len(sys.argv) < 2:
        print('Requires zip name argument.')
        sys.exit(1)

    print('Writing zip file', sys.argv[1])

    zf = zipfile.ZipFile('./../'+sys.argv[1], 'w')

    for file_structure in (src_files, test_files):
        for f in file_structure['include']:
            if not file_structure.get('glob', False):
                print('    Adding', file_structure['src_path']+f)
                zf.write(file_structure['src_path']+f, file_structure['dest_path']+f)
            else:
                print('    Adding files matching', file_structure['src_path']+f)
                for f2 in glob.glob(os.path.join(file_structure['src_path'], f)):
                    print('     Adding', f2)
                    zf.write(
                        f2,
                        os.path.join(file_structure['dest_path'], f2)
                    )
    print('Removing temp directory...')
    os.chdir('..')
    shutil.rmtree('__temp')
    print('Done.')


def main():
    with open('zip_structure.json') as f:
        do_zip_assemble(json.load(f))


if __name__ == '__main__':
    main()