#ifndef _NCGL_IMAGE_
#define _NCGL_IMAGE_

#include <GL/glew.h>
#include <GL/gl.h>
#include <iostream>
#include <fstream>
#include <Magick++.h>
using namespace Magick;
using namespace std;

class ncglImage {
public:
    ncglImage();
    ncglImage(unsigned int w, unsigned int h, GLenum target, GLenum format , bool mipmap = false);
    ~ncglImage();

    void 			setFilters(GLenum minfilter, GLenum magfilter);
    void 			setWrapMode(GLenum Xfilter, GLenum Yfilter);
    void 			load(const char* imagefile, GLenum target, GLenum format, bool mipmap = false);
    void 			load(Blob imageblob, GLenum target, GLenum format, bool mipmap = false);
	void 			load(Image* image, GLenum target, GLenum format, bool mipmap = false);
	void 			load(void* data, unsigned int w, unsigned int h, GLenum target, GLenum format, bool mipmap = false);
	void 			write(GLenum dataformat,GLenum datatype,void*& data,int lod=0);
	void 			write(GLenum dataformat,GLenum datatype,Blob* imageblob,int lod=0,bool flip=false,char* format="JP2",int quality=85);
	void 			write(GLenum dataformat,GLenum datatype,Image* image,int lod=0,bool flip=false);
	void			write(GLenum dataformat,GLenum datatype,const char* filename,int lod=0,bool flip=false);
	void 			init(unsigned int w, unsigned int h, GLenum target, GLenum format, bool mipmap=false, GLenum dataformat=GL_RGBA, GLenum datatype=GL_UNSIGNED_BYTE, void* data = 0);
    void 			loadData(void* data,GLenum dataformat,GLenum datatype);
    void 			swap(GLuint& tex_,GLuint& fbo_);
    GLuint 			getID() {
        return texID;
    };
    unsigned int	getWidth()  {
        return width;
    };
    unsigned int	getHeight()	{
        return height;
    };
    GLenum			getType()	{
        return type;
    };
    bool			hasMipMap()	{
        return hasmipmap;
    };

private:
    char imfile[200];
    GLenum type;
    GLenum internalformat;
    GLuint texID;
    bool hasmipmap;
    unsigned int width;
    unsigned int height;
};

template <class t_stream>
t_stream & operator<<(t_stream & stream_, const ncglImage & im_) {
    stream_ << "\nSize = " << im_.width << " x " << im_.height << endl;
    stream_ << "\nType = " << im_.type << endl;
    stream_ << "\nInternalformat = " << im_.internalformat << endl;
    stream_ << "\nHasmipmap = " << im_.hasmipmap << endl;
    stream_ << "\nTexID = " << im_.texID << endl;
    return stream_;
};


#endif
