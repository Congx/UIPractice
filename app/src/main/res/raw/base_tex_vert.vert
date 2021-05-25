attribute vec4 a_Position; //变量 float[4]  一个顶点  java传过来的

attribute vec4 a_TexCoord;  //纹理坐标
// 投影矩阵
//uniform mat4 u_Projection_Matrix;

varying vec2 v_TexCoord;

void main(){
    gl_Position = a_Position;
    v_TexCoord = a_TexCoord.xy;
}