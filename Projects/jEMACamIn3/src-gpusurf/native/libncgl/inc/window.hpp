#ifndef _NCGL_WINDOW_
#define _NCGL_WINDOW_

#include <stdarg.h>
#include <GL/glew.h>
#include <GL/gl.h>
#include <GL/glut.h>

class ncglWindow {

public:
	ncglWindow();
	~ncglWindow();
	void init(const char* name,int w, int h, bool vis = true);
	void setDisplayFunc(void (*df)());
	void setReshapeFunc(void (*rf)(int w ,int h));
	void setKeyboardFunc(void (*kf)(unsigned char key, int x, int y));
	void setSpecialFunc(void (*sf)(int key, int x, int y));
	void setMouseFunc(void (*mf)(int button,int state,int x,int y));
    void setMotionFunc(void (*mf)(int x,int y));
    void setIdleFunc(void (*idf)());

	void swapBuffers();
	void postRedisplay();
	void show();
	void hide();
	void start();
	void reshapeWindow(int w, int h);
	void setWindowTitle(char* title);
	int  getWidth(){return glutGet(GLUT_WINDOW_WIDTH);};
	int  getHeight(){return glutGet(GLUT_WINDOW_HEIGHT);};
	void renderBitmapString(float x,float y,float z,const char *cp_format,...);
	int  getID();

private:
	bool visible;

	void (*displayfunc )();
	void (*reshapefunc )(int w ,int h);
	void (*keyboardfunc)(unsigned char key, int x, int y);
	void (*specialfunc)(int key, int x, int y);
	void (*mousefunc)(int button,int state,int x,int y);
	void (*motionfunc)(int x,int y);
	void (*idlefunc )();

	int glutID;
    static bool initialized;

};

#endif
