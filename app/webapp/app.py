import os
import urllib.request
from flask import Flask, flash, request, redirect, render_template, make_response
from werkzeug.utils import secure_filename

UPLOAD_FOLDER = './uploads'
ALLOWED_EXTENSIONS = set(['txt', 'pdf']) # 'pdf' is kept as an example

app = Flask(__name__)
app.secret_key = "secret key"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

# Multiple routes are possible for a given view like album()
# Dynamic routes (types: string, int, float, path)
#@app.route('/<int:year>/<int:month>/<title>')
#def album(year, month, title):

# Template engine is Jinja2
#@app.route('/notes2')
#def upload_form():
#    return render_template('upload.html', title='Title')

@app.route('/notes2', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        print(request)
        print(request.headers)
        print(request.values)
        # Check if the post request has the file part
        if 'file' not in request.files:
            print('No file part')
            return 'NOK'
        file = request.files['file']
        if file.filename == '':
            print('Filename is empty')
            return 'NOK'
        if file and allowed_file(file.filename):
            print(file.filename + ' successfully uploaded')
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            return make_response('OK', 200)
        else:
            print('Wrong file type')
            return 'NOK'

if __name__ == "__main__":
    # HTTPS related: https://blog.miguelgrinberg.com/post/running-your-flask-application-over-https
    app.run(debug = True, host = '0.0.0.0', port = '5005')
