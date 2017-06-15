import turtle

def min(a, b):
    if (a < b):
        return a
    return b

def drawgrid():
    for i in range(int(len_vert / step_mark * 2)):
        a.forward(step_mark)
        a.left(90)
        a.forward(len_mark)
        a.left(180)
        a.forward(len_mark * 2)
        a.left(180)
        a.forward(len_mark)
        a.right(90)

a = turtle.Turtle()
a.shape("arrow")

turtle.screensize(1500, 1000)

len_vert = 490
len_hor = 1900
len_mark = 5
step_mark = 20
#draw coordinate system
a.speed(100)
a.pensize(1)
a.penup()
a.setpos(0 - len_hor / 2, 0)
a.pendown()
a.forward(len_hor)
a.stamp()
a.setpos(0 - len_hor / 2, 0)
a.left(90)
a.forward(len_vert)
a.stamp()
a.back(len_vert * 2)
#drawgrid()
a.setpos(0 - len_hor / 2, 0)
a.right(90)
#drawgrid()
file = open("wav-decoded.txt", "r")
l = turtle.Turtle()
r = turtle.Turtle()
l.setpos(0 - len_hor / 2, 0)
r.setpos(0 - len_hor / 2, 0)
l.speed(100)
r.speed(100)
l.pencolor("red")
r.pencolor("green")

numValues = 600
valuesPerLine = 8
valueGap = 5
text = ""
for i in range(int(numValues / valuesPerLine + 1)):
    text += file.readline()
endidx = 0
for i in range(numValues):
   startidx = min(text.find("+", endidx), text.find("-", endidx))
   endidx = text.find("\t", startidx)
   l.setpos(i * valueGap - len_hor / 2, int(text[startidx:endidx]))
   startidx = min(text.find("+", endidx), text.find("-", endidx))
   endidx = text.find("\t", startidx)
   r.setpos(i * valueGap  - len_hor / 2, int(text[startidx:endidx]))
        

file.close()

