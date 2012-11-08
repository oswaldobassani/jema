#ifndef _NCGL_FRAMEBUFFER_
#define _NCGL_FRAMEBUFFER_

#include "image.hpp"

class ncglFrameBuffer {

public:
	ncglFrameBuffer();
	~ncglFrameBuffer();

	void			Activate();
	void			Deactivate();
	void			Attach(GLenum ap, ncglImage* im, int lod=0);
	GLuint			getID() { return FBO;};
	unsigned int	getWidth()  { return w_FBO;};
	unsigned int	getHeight() { return h_FBO;};


private:
	GLuint				FBO;
	unsigned int		w_FBO,h_FBO;
	bool				FBOactive;

	int                 backup_viewport[4];
	GLint               backup_fbo;
};

#endif
